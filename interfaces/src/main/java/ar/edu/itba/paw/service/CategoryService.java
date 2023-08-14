package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> getById(long categoryId);

    Category getByIdChecked(long restaurantId, long categoryId);

    Category create(long restaurantId, String name);

    List<Category> getByRestaurantSortedByOrder(long restaurantId);

    Category updateName(long categoryId, String name);

    void delete(long categoryId);

    void swapOrder(long restaurantId, int orderNum1, int orderNum2);

    void moveProduct(long productId, long newCategoryId);
}
