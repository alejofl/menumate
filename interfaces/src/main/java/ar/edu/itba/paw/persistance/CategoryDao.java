package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {

    Optional<Category> getById(long categoryId);

    Category create(long restaurantId, String name);

    List<Category> getByRestaurantSortedByOrder(long restaurantId);

    void delete(long categoryId);
}
