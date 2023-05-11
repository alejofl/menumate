package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.CategoryService;
import ar.edu.itba.paw.service.ImageService;
import ar.edu.itba.paw.service.ProductService;
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
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public long create(String name, String email, int specialty, long ownerUserId, String description, String address, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2) {
        long logoKey = imageService.create(logo);
        long portrait1Key = imageService.create(portrait1);
        long portrait2Key = imageService.create(portrait2);

        return restaurantDao.create(name, email, specialty, ownerUserId, description, address, maxTables, logoKey, portrait1Key, portrait2Key);
    }

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return restaurantDao.getById(restaurantId);
    }

    @Override
    public PaginatedResult<Restaurant> getActive(int pageNumber, int pageSize) {
        return restaurantDao.getActive(pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Restaurant> getSortedByNameAsc(int pageNumber, int pageSize) {
        return restaurantDao.getSortedByName(pageNumber, pageSize, "ASC");
    }

    @Override
    public PaginatedResult<Restaurant> getSortedByNameDesc(int pageNumber, int pageSize) {
        return restaurantDao.getSortedByName(pageNumber, pageSize, "DESC");
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
    public List<Pair<Category, List<Product>>> getMenu(long restaurantId) {
        List<Category> categories = categoryService.getByRestaurantSortedByOrder(restaurantId);
        List<Pair<Category, List<Product>>> menu = new ArrayList<>();
        for (Category category : categories) {
            menu.add(new Pair<>(category, productService.getByCategory(category.getCategoryId())));
        }
        return menu;
    }

    @Override
    public boolean delete(long restaurantId) {
        return restaurantDao.delete(restaurantId);
    }

    @Override
    public List<RestaurantTags> getTags(int restaurantId) {
        return restaurantDao.getTags(restaurantId);
    }

    @Override
    public boolean addTag(int restaurantId, int tagId) {
        return restaurantDao.addTag(restaurantId, tagId);
    }

    @Override
    public boolean removeTag(int restaurantId, int tagId) {
        return restaurantDao.removeTag(restaurantId, tagId);
    }
}
