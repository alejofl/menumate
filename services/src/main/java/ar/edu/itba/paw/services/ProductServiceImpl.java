package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.ProductDeletedException;
import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final static Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ImageDao imageDao;

    @Override
    public Optional<Product> getById(long productId) {
        Optional<Product> product = productDao.getById(productId);
        if (product.isPresent() && product.get().getDeleted())
            throw new ProductDeletedException();
        return product;
    }

    @Transactional
    @Override
    public Product create(long categoryId, String name, String description, byte[] image, BigDecimal price) {
        Long imageKey = null;
        if (image != null) {
            imageKey = imageDao.create(image);
        }
        return productDao.create(categoryId, name, description, imageKey, price);
    }

    private Product getAndVerifyForUpdate(long productId) {
        final Product product = productDao.getById(productId).orElse(null);
        if (product == null) {
            LOGGER.error("Attempted to update non-existing product id {}", productId);
            throw new ProductNotFoundException();
        }

        if (product.getDeleted()) {
            LOGGER.error("Attempted to update deleted product id {}", product.getProductId());
            throw new IllegalStateException("Cannot update deleted product");
        }

        return product;
    }

    @Transactional
    @Override
    public Product update(long productId, String name, BigDecimal price, String description) {
        final Product product = getAndVerifyForUpdate(productId);

        if (product.getPrice().equals(price)) {
            productDao.updateNameAndDescription(product, name, description);
            return product;
        }

        product.setDeleted(true);
        final Product newProduct = productDao.create(product.getCategoryId(), name, description, product.getImageId(), price);
        LOGGER.info("Logical-deleted product id {} and inserted {} to update price", product.getProductId(), newProduct.getProductId());
        productDao.stopPromotionsBySource(productId);
        return newProduct;
    }

    @Transactional
    @Override
    public void updateImage(long productId, byte[] image) {
        if (image == null || image.length == 0)
            return;

        final Product product = getAndVerifyForUpdate(productId);
        imageDao.update(product.getImageId(), image);

        LOGGER.info("Updated image of product id {}", product.getProductId());
    }

    @Transactional
    @Override
    public void delete(long productId) {
        productDao.delete(productId);
    }

    @Transactional
    @Override
    public Promotion createPromotion(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate, int discountPercentage) {
        if (discountPercentage <= 0 || discountPercentage > 100) {
            LOGGER.error("Attempted to create product with discount outside range {}", discountPercentage);
            throw new IllegalArgumentException("Discount must be in the range (0, 100]");
        }

        final Product source = productDao.getById(sourceProductId).orElseThrow(ProductNotFoundException::new);
        if (source.getDeleted() || !source.getAvailable()) {
            LOGGER.error("Attempted to create a promotion from a{} product", source.getDeleted() ? " deleted" : "n unavailable");
            throw new IllegalStateException("Product cannot be deleted nor unavailable");
        }

        if (endDate != null) {
            if (!endDate.isAfter(startDate)) {
                LOGGER.error("Attempted to create a promotion with endDate <= startDate");
                throw new IllegalArgumentException("endDate must be either null, or after startDate");
            }

            LocalDateTime now = LocalDateTime.now();
            if (!endDate.isAfter(now)) {
                LOGGER.error("Attempted to create a promotion with endDate <= now");
                throw new IllegalArgumentException("endDate must be either null or in the past");
            }
        }

        return productDao.createPromotion(source, startDate, endDate, discountPercentage);
    }

    @Override
    public Optional<Promotion> hasPromotionInRange(long sourceProductId, LocalDateTime startDate, LocalDateTime endDate) {
        if (!startDate.isBefore(endDate))
            throw new IllegalArgumentException("endDate must be after startDate");

        return productDao.hasPromotionInRange(sourceProductId, startDate, endDate);
    }

    @Transactional
    @Override
    public void stopPromotionByDestination(long destinationProductId) {
        productDao.stopPromotionByDestination(destinationProductId);
    }

    @Scheduled(cron = "0 * * * * ?")
    public void updatePromotionsByTime() {
        productDao.startActivePromotions();
        productDao.closeInactivePromotions();
    }
}
