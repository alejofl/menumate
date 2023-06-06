package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.persistance.ProductDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public Product create(long categoryId, String name, String description, Long imageId, BigDecimal price) {
        final Product product = new Product(categoryId, name, description, imageId, price, true);
        em.persist(product);
        LOGGER.info("Created product with id {} for category {}", product.getProductId(), categoryId);
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
            throw new IllegalStateException("Product is already deleted");
        }

        product.setDeleted(true);

        Query query = em.createQuery("UPDATE Product SET deleted = true WHERE deleted = false AND EXISTS(FROM Promotion WHERE Promotion.source.productId = :sourceId AND Promotion.destination.productId = Product.productId)");
        query.setParameter("sourceId", product);
        int rows = query.executeUpdate();

        LOGGER.info("Logical-deleted product id {} alongside {} promotion copies", product.getProductId(), rows);
    }

    @Override
    public Promotion createPromotion(Product source, LocalDateTime startDate, LocalDateTime endDate, float discount) {
        BigDecimal promotionPrice = source.getPrice().multiply(BigDecimal.valueOf(discount));
        final Product destination = new Product(source.getCategoryId(), source.getName(), source.getDescription(), source.getImageId(), promotionPrice, true);
        em.persist(destination);
        em.flush();

        final Promotion promotion = new Promotion(source, destination, startDate, endDate, discount);
        em.persist(promotion);
        LOGGER.info("Created promotion with source id {} and destination id {} with a discount of {}", source.getProductId(), destination.getProductId(), discount);
        return promotion;
    }
}
