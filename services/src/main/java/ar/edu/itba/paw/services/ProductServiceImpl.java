package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductDao productDao;

    @Autowired
    private ImageService imageService;

    @Override
    public Product create(long categoryId, String name, String description, byte[] image, BigDecimal price) {
        long imageKey = imageService.create(image);
        return productDao.create(categoryId, name, description, imageKey, price);
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
    public boolean update(long productId, String name, BigDecimal price, String description) {
        return productDao.update(productId, name, price ,description);
    }

    @Override
    public boolean delete(long productId) {
        return productDao.delete(productId);
    }
}
