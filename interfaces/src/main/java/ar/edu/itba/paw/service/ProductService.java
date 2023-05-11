package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(long categoryId, String name, String description, byte[] image, BigDecimal price);

    Optional<Product> getById(long productId);

    List<Product> getByCategory(long categoryId);

    boolean update(long productId, String name, BigDecimal price, String description);

    boolean delete(long productId);
}
