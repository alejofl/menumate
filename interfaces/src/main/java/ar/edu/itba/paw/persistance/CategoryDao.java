package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryDao {
    Category createCategory(long restaurantId, String name, long order);
    Optional<Category> getCategoryById(long categoryId);
    List<Category> findByRestaurantId(long restaurantId);
    boolean updateName(long categoryId, String name);
    boolean updateOrder(long categoryId, long order);
    boolean deleteCategory(long categoryId);
}
