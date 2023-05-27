package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {

    Optional<Category> getById(long categoryId);

    Category create(long restaurantId, String name);

    List<Category> getByRestaurantSortedByOrder(long restaurantId);

    Category updateName(long categoryId, String name);

    Category updateOrder(long categoryId, int orderNum);

    void delete(long categoryId);
}
