package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Category create(long restaurantId, String name);

    Optional<Category> getById(long categoryId);

    List<Category> getByRestaurantSortedByOrder(long restaurantId);

    boolean updateName(long categoryId, String name);

    boolean updateOrder(long categoryId, int order);

    boolean delete(long categoryId);
}
