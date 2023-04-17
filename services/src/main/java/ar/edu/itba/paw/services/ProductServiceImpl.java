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

    @Autowired
    private ProductDao productDao;

    @Override
    public Product create(int categoryId, String name, double price) {
        return productDao.create(categoryId, name, price);
    }

    @Override
    public Optional<Product> getById(int productId) {
        return productDao.getById(productId);
    }

    @Override
    public List<Product> getByCategory(int categoryId) {
        return productDao.getByCategory(categoryId);
    }

    @Override
    public boolean updatePrice(int productId, double price) {
        return productDao.updatePrice(productId, price);
    }

    @Override
    public boolean updateName(int productId, String name) {
        return productDao.updateName(productId, name);
    }

    @Override
    public boolean delete(int productId) {
        return productDao.delete(productId);
    }
}
