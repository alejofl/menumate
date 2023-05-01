package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.model.util.Pair;
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

    @Autowired
    private ImageService imageService;

    @Override
    public Optional<Restaurant> getById(int restaurantId) {
        return restaurantDao.getById(restaurantId);
    }

    @Override
    public PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize) {
        return restaurantDao.getActive(pageNumber, pageSize);
    }

    @Override
    public int getActiveCount() {
        return restaurantDao.countActive();
    }

    @Override
    public PaginatedResult<Restaurant> getSearchResults(String query, int pageNumber, int pageSize) {
        if (query == null)
            query = "";

        String[] tokens = query.toLowerCase().split(" +");
        return restaurantDao.getSearchResults(tokens, pageNumber, pageSize);
    }

    @Override
    public List<Pair<Category, List<Product>>> getMenu(int restaurantId) {
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
    public int create(String name, String description, String address, User owner, byte[] logo, byte[] portrait1, byte[] portrait2) {
        int logoKey = imageService.create(logo);
        int portrait1Key = imageService.create(portrait1);
        int portrait2Key = imageService.create(portrait2);

        return restaurantDao.create(name, description, address, owner.getEmail(), owner.getUserId(), logoKey, portrait1Key, portrait2Key);
    }

    @Override
    public boolean delete(int restaurantId) {
        return restaurantDao.delete(restaurantId);
    }
}
