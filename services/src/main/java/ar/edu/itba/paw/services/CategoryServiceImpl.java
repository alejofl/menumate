package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Category create(int restaurantId, String name) {
        return categoryDao.create(restaurantId, name);
    }

    @Override
    public Optional<Category> getById(int categoryId) {
        return categoryDao.getById(categoryId);
    }

    @Override
    public List<Category> getByRestaurantSortedByOrder(int restaurantId) {
        return categoryDao.getByRestaurantSortedByOrder(restaurantId);
    }

    @Override
    public boolean updateName(int categoryId, String name) {
        return categoryDao.updateName(categoryId, name);
    }

    @Override
    public boolean updateOrder(int categoryId, int order) {
        return categoryDao.updateOrder(categoryId, order);
    }

    @Override
    public boolean delete(int categoryId) {
        return categoryDao.delete(categoryId);
    }
}
