package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Category create(int restaurantId, String name, int order);

    Optional<Category> getById(int categoryId);

    List<Category> getByRestaurantSortedByOrder(int restaurantId);

    boolean updateName(int categoryId, String name);

    boolean updateOrder(int categoryId, int order);

    boolean delete(int categoryId);
}
