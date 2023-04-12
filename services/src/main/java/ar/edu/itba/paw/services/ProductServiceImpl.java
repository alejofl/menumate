package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductDao productDao;

    @Autowired
    public ProductServiceImpl(final ProductDao productDao) {
        this.productDao = productDao;
    }
    @Override
    public Product createProduct(long categoryId, String name, double price) {
        return productDao.createProduct(categoryId, name, price);
    }

    @Override
    public Optional<Product> findProductById(long productId) {
        return productDao.findProductById(productId);
    }

    @Override
    public List<Product> findProductsByCategory(long categoryId) {
        return productDao.findProductsByCategory(categoryId);
    }

    @Override
    public boolean updateProductPrice(long productId, double price) {
        return productDao.updateProductPrice(productId, price);
    }

    @Override
    public boolean updateProductName(long productId, String name) {
        return productDao.updateProductName(productId, name);
    }

    @Override
    public boolean deleteProduct(long productId) {
        return productDao.deleteProduct(productId);
    }
}
