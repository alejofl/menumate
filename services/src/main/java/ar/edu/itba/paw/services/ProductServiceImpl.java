package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.service.ProductService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Override
    public Product create(long categoryId, String name, double price) {
        return productDao.create(categoryId, name, price);
    }

    @Override
    public Optional<Product> getById(long productId) {
        return productDao.getById(productId);
    }

    @Override
    public List<Product> getByCategory(long categoryId) {
        return productDao.getByCategory(categoryId);
    }

    @Override
    public boolean updatePrice(long productId, double price) {
        return productDao.updatePrice(productId, price);
    }

    @Override
    public boolean updateName(long productId, String name) {
        return productDao.updateName(productId, name);
    }

    @Override
    public boolean delete(long productId) {
        return productDao.delete(productId);
    }
}
