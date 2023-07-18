package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.CategoryDeletedException;
import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final static Logger LOGGER = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryDao categoryDao;

    @Override
    public Optional<Category> getById(long categoryId) {
        Optional<Category> category = categoryDao.getById(categoryId);
        if (category.isPresent() && category.get().getDeleted())
            throw new CategoryDeletedException();
        return category;
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
        if (!maybeCategory.isPresent()) {
            LOGGER.error("Attempted to update name of non-existing category id {}", categoryId);
            throw new CategoryNotFoundException();
        }

        final Category category = maybeCategory.get();
        if (category.getDeleted()) {
            LOGGER.error("Attempted to update name of deleted category id {}", categoryId);
            throw new CategoryDeletedException();
        }
        category.setName(name);
        LOGGER.error("Updated name of category id {}", categoryId);
        return category;
    }

    @Transactional
    @Override
    public void delete(long categoryId) {
        categoryDao.delete(categoryId);
    }

    @Transactional
    @Override
    public void swapOrder(long restaurantId, int orderNum1, int orderNum2) {
        if (orderNum1 == orderNum2) {
            LOGGER.warn("Attempted to swapOrder between categories of restaurant id {} with the same order {}", restaurantId, orderNum1);
            return;
        }

        categoryDao.swapOrder(restaurantId, orderNum1, orderNum2);
    }

    @Transactional
    @Override
    public void moveProduct(long productId, long newCategoryId) {
        categoryDao.moveProduct(productId, newCategoryId);
    }
}
