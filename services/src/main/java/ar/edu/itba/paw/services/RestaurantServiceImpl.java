package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.RestaurantService;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ProductDao productDao;

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return restaurantDao.getById(restaurantId);
    }

    @Override
    public List<Restaurant> getAll() {
        return restaurantDao.getAll();
    }

    @Override
    public List<Pair<Category, List<Product>>> getMenu(long restaurantId) {
        List<Product> products = productDao.getByRestaurantOrderByCategoryOrder(restaurantId);

        List<Pair<Category, List<Product>>> menu = new ArrayList<>();
        Category currentCategory = null;
        List<Product> currentList = null;
        for (Product product : products) {
            if (currentCategory == null || currentCategory.getCategoryId() != product.getCategory().getCategoryId()) {
                if (currentCategory != null)
                    menu.add(new Pair<>(currentCategory, currentList));
                currentCategory = product.getCategory();
                currentList = new ArrayList<>();
            }
            currentList.add(product);
        }

        if (currentCategory != null)
            menu.add(new Pair<>(currentCategory, currentList));

        return menu;
    }

    @Override
    public Restaurant create(String name, String email) {
        return restaurantDao.create(name, email);
    }

    @Override
    public boolean delete(long restaurantId) {
        return restaurantDao.delete(restaurantId);
    }
}
