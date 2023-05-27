package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Optional<Category> getById(long categoryId) {
        return categoryDao.getById(categoryId);
    }

    @Transactional
    @Override
    public Category create(long restaurantId, String name) {
        return categoryDao.create(restaurantId, name);
    }

    @Override
    public List<Category> getByRestaurantSortedByOrder(long restaurantId) {
        return categoryDao.getByRestaurantSortedByOrder(restaurantId);
    }

    @Transactional
    @Override
    public Category updateName(long categoryId, String name) {
        final Optional<Category> maybeCategory = categoryDao.getById(categoryId);
        if (!maybeCategory.isPresent())
            throw new CategoryNotFoundException();

        final Category category = maybeCategory.get();
        category.setName(name);
        return category;
    }

    @Transactional
    @Override
    public Category updateOrder(long categoryId, int orderNum) {
        final Optional<Category> maybeCategory = categoryDao.getById(categoryId);
        if (!maybeCategory.isPresent())
            throw new CategoryNotFoundException();

        final Category category = maybeCategory.get();
        category.setOrderNum(orderNum);
        return category;
    }

    @Transactional
    @Override
    public void delete(long categoryId) {
        categoryDao.delete(categoryId);
    }
}
