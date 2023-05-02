package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Product create(int categoryId, String name, String description, byte[] image, double price);

    Optional<Product> getById(int productId);

    List<Product> getByCategory(int categoryId);

    boolean updatePrice(int productId, double price);

    boolean updateName(int productId, String name);

    boolean delete(int productId);

}
