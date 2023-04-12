package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    Category create(long restaurantId, String name, int order);

    Optional<Category> getById(long categoryId);

    List<Category> getByRestaurant(long restaurantId);

    boolean updateName(long categoryId, String name);

    boolean updateOrder(long categoryId, int order);

    boolean delete(long categoryId);
}
