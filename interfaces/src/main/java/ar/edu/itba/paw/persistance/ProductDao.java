package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Product;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductDao {

    Optional<Product> getById(long productId);

    Product create(long categoryId, String name, String description, Long imageId, BigDecimal price);

    void delete(long productId);
}
