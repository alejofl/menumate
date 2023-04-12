package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product createProduct(long categoryId, String name, double price);
    Optional<Product> findProductById(long productId);
    List<Product> findProductsByCategory(long categoryId);
    boolean updateProductPrice(long productId, double price);
    boolean updateProductName(long productId, String name);
    boolean deleteProduct(long productId);

}
