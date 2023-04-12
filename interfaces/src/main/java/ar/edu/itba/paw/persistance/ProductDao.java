package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product createProduct(long categoryId, String name, double price);
    Optional<Product> findProductById(long productId);
    List<Product> findProductsByCategory(long categoryId);
    void updateProductPrice(long productId, double price);
    void updateProductName(long productId, String name);
    void deleteProduct(long productId);

}
