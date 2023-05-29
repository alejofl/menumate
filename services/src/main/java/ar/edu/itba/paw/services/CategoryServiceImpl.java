package ar.edu.itba.paw.services;

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
        if (!maybeCategory.isPresent()) {
            LOGGER.error("Attempted to update name of non-existing category id {}", categoryId);
            throw new CategoryNotFoundException();
        }

        final Category category = maybeCategory.get();
        category.setName(name);
        LOGGER.error("Updated name of category id {}", categoryId);
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
        LOGGER.error("Updated order of category id {} to {}", categoryId, orderNum);
        return category;
    }

    @Transactional
    @Override
    public void delete(long categoryId) {
        categoryDao.delete(categoryId);
    }
}
