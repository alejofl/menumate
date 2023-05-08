package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Product create(int categoryId, String name, String description, int imageId, BigDecimal price);

    Optional<Product> getById(int productId);

    List<Product> getByCategory(int categoryId);

    List<Product> getByRestaurantOrderByCategoryOrder(int restaurantId);

    boolean updatePrice(int productId, BigDecimal price);

    boolean updateName(int productId, String name);

    boolean delete(int productId);

}
