package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

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
        if (!maybeProduct.isPresent())
            throw new ProductNotFoundException();

        final Product product = maybeProduct.get();
        product.setName(name);
        product.setDescription(description);
        // TODO: Implement update price
        return product;
    }

    @Transactional
    @Override
    public void delete(long productId) {
        productDao.delete(productId);
    }
}
