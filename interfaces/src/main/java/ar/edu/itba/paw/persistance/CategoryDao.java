package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    long create(long restaurantId, String name);

    Optional<Category> getById(long categoryId);

    List<Category> getByRestaurantSortedByOrder(long restaurantId);

    void updateName(long categoryId, String name);

    void updateOrder(long categoryId, int order);

    void delete(long categoryId);
}
