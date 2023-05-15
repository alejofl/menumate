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
    public long create(long restaurantId, String name) {
        return categoryDao.create(restaurantId, name);
    }

    @Override
    public Optional<Category> getById(long categoryId) {
        return categoryDao.getById(categoryId);
    }

    @Override
    public List<Category> getByRestaurantSortedByOrder(long restaurantId) {
        return categoryDao.getByRestaurantSortedByOrder(restaurantId);
    }

    @Override
    public void updateName(long categoryId, String name) {
        categoryDao.updateName(categoryId, name);
    }

    @Override
    public void updateOrder(long categoryId, int order) {
        categoryDao.updateOrder(categoryId, order);
    }

    @Override
    public void delete(long categoryId) {
        categoryDao.delete(categoryId);
    }
}
