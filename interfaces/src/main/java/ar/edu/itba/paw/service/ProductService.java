package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(long categoryId, String name, String description, byte[] image, BigDecimal price);

    Optional<Product> getById(long productId);

    List<Product> getByCategory(long categoryId);

    boolean updatePrice(long productId, BigDecimal price);

    boolean updateName(long productId, String name);

    boolean delete(long productId);
}
