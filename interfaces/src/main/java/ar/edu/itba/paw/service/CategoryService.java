package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

        Category createCategory(long restaurantId, String name, long order);
        Optional<Category> getCategoryById(long categoryId);
        List<Category> findByRestaurantId(long restaurantId);
        void updateName(long categoryId, String name);
        void updateOrder(long categoryId, long order);
        void deleteCategory(long categoryId);
}
