package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.ProductDeletedException;
import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.exception.PromotionNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.persistance.ProductDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductJpaDao implements ProductDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Product> getById(long productId) {
        return Optional.ofNullable(em.find(Product.class, productId));
    }

    @Override
    public Optional<Promotion> getPromotionById(long promotionId) {
        return Optional.ofNullable(em.find(Promotion.class, promotionId));
    }

    @Override
    public Product create(long categoryId, String name, String description, Long imageId, BigDecimal price) {
        final Product product = new Product(categoryId, name, description, imageId, price, true);
        em.persist(product);
        LOGGER.info("Created product with id {} for category {}", product.getProductId(), categoryId);
        return product;
    }

    @Override
    public Product create(Category category, String name, String description, Long imageId, BigDecimal price) {
        final Product product = new Product(category, name, description, imageId, price, true);
        em.persist(product);
        LOGGER.info("Created product with id {} for category {}", product.getProductId(), category.getCategoryId());
        return product;
    }

    @Override
    public void delete(long productId) {
        final Product product = em.find(Product.class, productId);
        if (product == null) {
            LOGGER.error("Attempted to delete non-existing product id {}", productId);
            throw new ProductNotFoundException();
        }

        if (product.getDeleted()) {
            LOGGER.error("Attempted to delete already-deleted product id {}", product.getProductId());
            throw new ProductDeletedException();
        }

        product.setDeleted(true);
        product.setAvailable(false);

        // Delete products from promotions generated with this product
        Query query = em.createQuery("UPDATE Product p SET p.deleted = true, p.available = false WHERE p.deleted = false AND EXISTS(FROM Promotion r WHERE r.source.productId = :sourceId AND r.destination.productId = p.productId)");
        query.setParameter("sourceId", product.getProductId());
        int rows = query.executeUpdate();

        // Close any promotions generated with this product
        Query promoQuery = em.createQuery("UPDATE Promotion SET endDate = now() WHERE source.productId = :sourceId AND (endDate IS NULL OR endDate > now())");
        promoQuery.setParameter("sourceId", product.getProductId());
        int promoRows = promoQuery.executeUpdate();

        LOGGER.info("Logical-deleted product id {} alongside {} promotion copies and closed {} promotions", product.getProductId(), rows, promoRows);
    }

    @Override
    public Promotion createPromotion(Product source, LocalDateTime startDate, LocalDateTime endDate, BigDecimal discountPercentage) {
        BigDecimal hundred = BigDecimal.valueOf(100);
        BigDecimal promotionPrice = source.getPrice().multiply(hundred.subtract(discountPercentage)).divide(hundred, 2, RoundingMode.FLOOR);
        final Product destination = new Product(source.getCategory(), source.getName(), source.getDescription(), source.getImageId(), promotionPrice, true);
        em.persist(destination);
        em.flush();

        final Promotion promotion = new Promotion(source, destination, startDate, endDate);
        em.persist(promotion);
        if (promotion.isActive()) {
            source.setAvailable(false);
            destination.setAvailable(true);
        } else {
            destination.setAvailable(false);
        }
        LOGGER.info("Created promotion with source id {} and destination id {} with a discount of {}", source.getProductId(), destination.getProductId(), discountPercentage);
        return promotion;
    }

    @Override
    public Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate) {
        final Query nativeQuery = em.createNativeQuery("SELECT promotion_id FROM promotions WHERE source_id = :sourceId AND (end_date IS NULL OR start_date < end_date) AND (end_date IS NULL OR end_date >= :startDate) AND start_date < :endDate LIMIT 1");
        nativeQuery.setParameter("sourceId", sourceProductId);
        nativeQuery.setParameter("startDate", startDate);
        nativeQuery.setParameter("endDate", endDate);

        final List<?> resultList = nativeQuery.getResultList();
        if (resultList.isEmpty())
            return Optional.empty();

        long promotionId = ((Number) resultList.get(0)).longValue();
        return Optional.of(em.find(Promotion.class, promotionId));
    }

    @Override
    public void updateNameAndDescription(Product product, String name, String description) {
        product.setName(name);
        product.setDescription(description);

        // Update active promotion copies
        Query query = em.createQuery(
                "UPDATE Product p SET p.name = :name, p.description = :description WHERE p.deleted = false AND" +
                        " EXISTS(FROM Promotion WHERE destination.productId = p.productId AND source.productId = :productId)"
        );
        query.setParameter("productId", product.getProductId());
        query.setParameter("name", name);
        query.setParameter("description", description);
        int rows = query.executeUpdate();

        LOGGER.info("Updated name and description of product id {} alongside {} promotion cop{}", product.getProductId(), rows, rows == 1 ? "y" : "ies");
    }

    @Override
    public void stopPromotion(long restaurantId, long promotionId) {
        TypedQuery<Promotion> promoQuery = em.createQuery(
                "FROM Promotion WHERE promotionId = :promotionId AND source.category.restaurantId = :restaurantId",
                Promotion.class
        );
        promoQuery.setParameter("restaurantId", restaurantId);
        promoQuery.setParameter("promotionId", promotionId);

        Promotion promotion = promoQuery.getResultList().stream().findFirst().orElseThrow(PromotionNotFoundException::new);
        if (promotion.hasEnded()) {
            LOGGER.error("Attempted to stop an already-ended promotion id {}", promotion.getPromotionId());
            throw new InvalidUserArgumentException("Cannot stop a promotion that has already ended");
        }

        promotion.setEndDate(LocalDateTime.now());
        promotion.getDestination().setDeleted(true);
        promotion.getDestination().setAvailable(false);
        promotion.getSource().setAvailable(true);
        LOGGER.info("Stopped promotion id {} by updating end date, logical-deleted product id {}", promotion.getPromotionId(), promotion.getDestination().getProductId());
    }

    @Override
    public void stopPromotionsBySource(long sourceProductId) {
        final Product product = getById(sourceProductId).orElseThrow(PromotionNotFoundException::new);
        product.setAvailable(true);

        // Delete products from promotions generated with this product
        Query delQuery = em.createQuery("UPDATE Product p SET p.deleted = true WHERE p.deleted = false AND EXISTS(FROM Promotion r WHERE r.source.productId = :sourceId AND r.destination.productId = p.productId)");
        delQuery.setParameter("sourceId", sourceProductId);
        int delRows = delQuery.executeUpdate();

        // Close any promotions generated with this product
        Query promoQuery = em.createQuery("UPDATE Promotion SET endDate = now() WHERE source.productId = :sourceId AND (endDate IS NULL OR endDate > now())");
        promoQuery.setParameter("sourceId", sourceProductId);
        int promoRows = promoQuery.executeUpdate();

        LOGGER.info("Closed {} promotion{} by updating end date, logical-deleted {} product{}", delRows, delRows == 1 ? "" : "s", promoRows, promoRows == 1 ? "" : "s");
    }

    @Transactional
    @Override
    public void startActivePromotions() {
        // Set as unavailable all products that have an active promotion
        Query unavQuery = em.createQuery("UPDATE Product p SET p.available = false WHERE p.available = true AND p.deleted = false" +
                " AND EXISTS(FROM Promotion WHERE source.productId = p.productId AND startDate <= now() AND (endDate IS NULL OR endDate > now()))"
        );
        int unavRows = unavQuery.executeUpdate();

        // Set as available all destination products from those promotions
        Query avQuery = em.createQuery("UPDATE Product p SET p.available = true WHERE p.available = false AND p.deleted = false" +
                " AND EXISTS(FROM Promotion WHERE destination.productId = p.productId AND startDate <= now() AND (endDate IS NULL OR endDate > now()))"
        );
        int avRows = avQuery.executeUpdate();

        LOGGER.info("Started active promotions: {} source products disabled and {} destination products enabled", unavRows, avRows);
    }

    @Transactional
    @Override
    public void closeInactivePromotions() {
        // Set as available all products whose promotion ended and don't have another active promotion
        Query avQuery = em.createQuery(
                "UPDATE Product p SET p.available = true WHERE p.available = false" +
                        " AND EXISTS(FROM Promotion WHERE source.productId = p.productId AND endDate IS NOT NULL AND endDate <= now())" +
                        " AND NOT EXISTS(FROM Promotion WHERE source.productId = p.productId AND startDate <= now() AND (endDate IS NULL OR endDate > now()))"
        );
        int avRows = avQuery.executeUpdate();

        // Logical-delete all products from inactive promotions
        Query delQuery = em.createQuery(
                "UPDATE Product p SET p.deleted = true WHERE p.deleted = false AND" +
                        " EXISTS(FROM Promotion WHERE destination.productId = p.productId AND endDate IS NOT NULL AND endDate <= now())"
        );
        int delRows = delQuery.executeUpdate();

        LOGGER.info("Closed inactive promotions: {} products made available and {} deleted", avRows, delRows);
    }

    @Override
    public boolean areAllProductsFromRestaurant(long restaurantId, List<Long> productIds) {
        Query query = em.createQuery("SELECT COUNT(p.productId) FROM Product p JOIN Category c ON p.categoryId = c.categoryId WHERE p.productId IN :productIds AND c.restaurantId = :restaurantId AND p.available = true AND p.deleted = false");
        query.setParameter("restaurantId", restaurantId);
        query.setParameter("productIds", productIds);
        int count = ((Number) query.getSingleResult()).intValue();

        return count == productIds.stream().distinct().count();
    }
}
