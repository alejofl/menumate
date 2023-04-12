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

    private final CategoryDao categoryDao;

    @Autowired
    public CategoryServiceImpl(final CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Category createCategory(long restaurantId, String name, long order) {
        return categoryDao.createCategory(restaurantId, name, order);
    }

    @Override
    public Optional<Category> getCategoryById(long categoryId) {
         return categoryDao.getCategoryById(categoryId);
    }

    @Override
    public List<Category> findByRestaurantId(long restaurantId) {
        return categoryDao.findByRestaurantId(restaurantId);
    }

    @Override
    public boolean updateName(long categoryId, String name) {
        return categoryDao.updateName(categoryId, name);
    }

    @Override
    public boolean updateOrder(long categoryId, long order) {
        return categoryDao.updateOrder(categoryId, order);
    }

    @Override
    public boolean deleteCategory(long categoryId) {
        return categoryDao.deleteCategory(categoryId);
    }
}
