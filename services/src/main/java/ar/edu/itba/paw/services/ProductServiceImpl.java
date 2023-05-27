package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
        return productDao.getById(productId);
    }

    @Transactional
    @Override
    public Product create(long categoryId, String name, String description, byte[] image, BigDecimal price) {
        long imageKey = imageDao.create(image);
        return productDao.create(categoryId, name, description, imageKey, price);
    }

    @Transactional
    @Override
    public Product update(long productId, String name, BigDecimal price, String description) {
        final Optional<Product> maybeProduct = productDao.getById(productId);
        if (!maybeProduct.isPresent()) {
            LOGGER.error("Attempted to update non-existing product id {}", productId);
            throw new ProductNotFoundException();
        }

        final Product product = maybeProduct.get();

        if (product.getPrice().equals(price)) {
            product.setName(name);
            product.setDescription(description);
            LOGGER.info("Updated name and description of product id {}", product.getProductId());
            return product;
        }

        product.setDeleted(true);
        final Product newProduct = productDao.create(product.getCategoryId(), name, description, product.getImageId(), price);
        LOGGER.info("Logical-deleted product id {} and inserted {} to update price", product.getProductId(), newProduct.getProductId());
        return newProduct;
    }

    @Transactional
    @Override
    public void delete(long productId) {
        productDao.delete(productId);
    }
}
