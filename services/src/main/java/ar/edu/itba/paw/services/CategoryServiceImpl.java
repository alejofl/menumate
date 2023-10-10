package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.CategoryDeletedException;
import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.exception.RestaurantDeletedException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
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

    @Autowired
    private RestaurantDao restaurantDao;

    @Override
    public Optional<Category> getById(long categoryId) {
        return categoryDao.getById(categoryId);
    }

    @Override
    public Category getByIdChecked(long restaurantId, long categoryId, boolean allowDeleted) {
        final Optional<Category> maybeCategory = categoryDao.getById(categoryId);
        if (!maybeCategory.isPresent()) {
            if (!restaurantDao.getById(restaurantId).isPresent())
                throw new RestaurantNotFoundException();
            throw new CategoryNotFoundException();
        }

        final Category category = maybeCategory.get();
        if (category.getRestaurantId() != restaurantId)
            throw new CategoryNotFoundException();
        if (!allowDeleted && category.getDeleted())
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
        final List<Category> categories = categoryDao.getByRestaurantSortedByOrder(restaurantId);

        if (categories.isEmpty()) {
            final Optional<Restaurant> restaurant = restaurantDao.getById(restaurantId);
            if (!restaurant.isPresent())
                throw new RestaurantNotFoundException();
            else if (restaurant.get().getDeleted())
                throw new RestaurantDeletedException();
        }

        return categories;
    }

    @Transactional
    @Override
    public Category updateName(long restaurantId, long categoryId, String name) {
        final Category category = getByIdChecked(restaurantId, categoryId, false);
        category.setName(name);
        LOGGER.error("Updated name of category id {}", categoryId);
        return category;
    }

    @Transactional
    @Override
    public void delete(long restaurantId, long categoryId) {
        getByIdChecked(restaurantId, categoryId, false);
        categoryDao.delete(categoryId);
    }

    @Transactional
    @Override
    public void setOrder(Category category, int orderNum) {
        if (category.getOrderNum() == orderNum) {
            LOGGER.warn("Attempted to setOrder to category {} with the same orderNum {}", category.getCategoryId(), orderNum);
            return;
        }

        categoryDao.setOrder(category, orderNum);
    }

    @Transactional
    @Override
    public void moveProduct(long productId, long newCategoryId) {
        categoryDao.moveProduct(productId, newCategoryId);
    }
}
