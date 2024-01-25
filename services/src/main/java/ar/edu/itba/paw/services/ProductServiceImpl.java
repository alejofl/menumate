package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.*;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Image;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.CategoryService;
import ar.edu.itba.paw.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductDao productDao;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ImageDao imageDao;

    @Override
    public Optional<Product> getById(long productId) {
        Optional<Product> product = productDao.getById(productId);
        if (product.isPresent() && product.get().getDeleted())
            throw new ProductDeletedException();
        return product;
    }

    @Override
    public Product getByIdChecked(long restaurantId, long categoryId, long productId, boolean allowDeleted) {
        final Optional<Product> maybeProduct = productDao.getById(productId);
        if (!maybeProduct.isPresent()) {
            if (!restaurantDao.getById(restaurantId).isPresent())
                throw new RestaurantNotFoundException();
            throw new ProductNotFoundException();
        }

        final Product product = maybeProduct.get();
        if (product.getCategoryId() != categoryId)
            throw new CategoryNotFoundException();
        if (product.getCategory().getRestaurantId() != restaurantId)
            throw new RestaurantNotFoundException();
        if (!allowDeleted && product.getDeleted())
            throw new ProductDeletedException();

        return product;
    }

    @Override
    public Promotion getPromotionById(long restaurantId, long promotionId) {
        return productDao.getPromotionById(promotionId)
                .filter(p -> p.getSource().getCategory().getRestaurantId() == restaurantId)
                .orElseThrow(PromotionNotFoundException::new);
    }

    @Transactional
    @Override
    public Product create(long restaurantId, long categoryId, String name, String description, Long imageId, BigDecimal price) {
        // Ensure the category exists under that restaurant, throw an appropriate exception otherwise.
        final Category category = categoryService.getByIdChecked(restaurantId, categoryId, false);

        Optional<Image> image = imageId == null? Optional.empty() : imageDao.getById(imageId);
        return productDao.create(category, name, description, image.map(Image::getImageId).orElse(null), price);
    }

    @Transactional
    @Override
    public Product update(long restaurantId, long categoryId, long productId, String name, BigDecimal price, String description) {
        final Product product = getByIdChecked(restaurantId, categoryId, productId, false);

        if (product.getPrice().equals(price)) {
            productDao.updateNameAndDescription(product, name, description);
            return product;
        }

        product.setDeleted(true);
        product.setAvailable(false);
        final Product newProduct = productDao.create(product.getCategoryId(), name, description, product.getImageId(), price);
        LOGGER.info("Logical-deleted product id {} and inserted {} to update price", product.getProductId(), newProduct.getProductId());
        productDao.stopPromotionsBySource(productId);
        return newProduct;
    }

    @Transactional
    @Override
    public void updateImage(long restaurantId, long categoryId, long productId, Optional<Long> imageId) {
        final Product product = getByIdChecked(restaurantId, categoryId, productId, false);
        imageId.ifPresent(product::setImageId);

        LOGGER.info("Updated image of product id {}", product.getProductId());
    }

    @Transactional
    @Override
    public void delete(long restaurantId, long categoryId, long productId) {
        // Check that the product exists under said category and said restaurant.
        getByIdChecked(restaurantId, categoryId, productId, false);

        productDao.delete(productId);
    }

    @Transactional
    @Override
    public Promotion createPromotion(long restaurantId, long sourceProductId, LocalDateTime startDate, LocalDateTime endDate, BigDecimal discountPercentage) {
        if (discountPercentage.compareTo(BigDecimal.ONE) < 0 || discountPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
            LOGGER.error("Attempted to create product with discount outside range {}", discountPercentage);
            throw new IllegalArgumentException("exception.IllegalArgumentException.createPromotion.discountPercentage");
        }

        final Product source = productDao.getById(sourceProductId).orElseThrow(ProductNotFoundException::new);
        if (source.getCategory().getRestaurantId() != restaurantId) {
            LOGGER.error("Attempted to create a promotion with a product id {} that does not belong to the restaurant id {}", sourceProductId, restaurantId);
            throw new InvalidUserArgumentException("exception.InvalidUserArgumentException.createPromotion.sourceProduct");
        }

        if (source.getDeleted() || !source.getAvailable()) {
            LOGGER.error("Attempted to create a promotion from a{} product", source.getDeleted() ? " deleted" : "n unavailable");
            throw new InvalidUserArgumentException("exception.InvalidUserArgumentException.createPromotion.deletedOrUnavailableProduct");
        }

        if (endDate != null) {
            if (!endDate.isAfter(startDate)) {
                LOGGER.error("Attempted to create a promotion with endDate <= startDate");
                throw new IllegalArgumentException("exception.IllegalArgumentException.createPromotion.endDateVSStartDate");
            }

            LocalDateTime now = LocalDateTime.now();
            if (!endDate.isAfter(now)) {
                LOGGER.error("Attempted to create a promotion with endDate <= now");
                throw new IllegalArgumentException("exception.IllegalArgumentException.createPromotion.endDateVSNow");
            }
        }

        return productDao.createPromotion(source, startDate, endDate, discountPercentage);
    }

    @Override
    public Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isBefore(endDate))
            throw new IllegalArgumentException("exception.IllegalArgumentException.hasPromotionInRange.endDateVsStartDate");

        return productDao.hasPromotionInRange(sourceProductId, startDate, endDate);
    }

    @Transactional
    @Override
    public void stopPromotion(long restaurantId, long promotionId) {
        productDao.stopPromotion(restaurantId, promotionId);
    }

    @Override
    public boolean areAllProductsFromRestaurant(long restaurantId, List<Long> productIds) {
        return productDao.areAllProductsFromRestaurant(restaurantId, productIds);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void updatePromotionsByTime() {
        productDao.startActivePromotions();
        productDao.closeInactivePromotions();
    }
}
