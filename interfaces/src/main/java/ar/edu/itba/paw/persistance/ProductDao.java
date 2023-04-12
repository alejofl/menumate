package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product create(long categoryId, String name, double price);

    Optional<Product> getById(long productId);

    List<Product> getByCategory(long categoryId);

    boolean updatePrice(long productId, double price);

    boolean updateName(long productId, String name);

    boolean delete(long productId);

}
