package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ImageDao imageDao;

    @Transactional
    @Override
    public Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2, boolean isActive, List<RestaurantTags> tags) {
        long logoId = imageDao.create(logo);
        long portrait1Id = imageDao.create(portrait1);
        long portrait2Id = imageDao.create(portrait2);

        return restaurantDao.create(name, email, specialty, ownerUserId, address, description, maxTables, logoId, portrait1Id, portrait2Id, isActive, tags);
    }

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return restaurantDao.getById(restaurantId);
    }

    @Override
    public PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties) {
        // NOTE: If we want for queries to "pizza" to include the tag for PIZZA, we can process the query and add the
        // tag in here.
        return restaurantDao.search(query, pageNumber, pageSize, orderBy, descending, tags, specialties);
    }

    @Override
    public List<Promotion> getActivePromotions(long restaurantId) {
        return restaurantDao.getActivePromotions(restaurantId);
    }

    private Restaurant getAndVerifyForUpdate(long restaurantId) {
        final Restaurant restaurant = restaurantDao.getById(restaurantId).orElse(null);
        if (restaurant == null) {
            LOGGER.error("Attempted to update nonexisting restaurant id {}", restaurantId);
            throw new RestaurantNotFoundException();
        }

        if (restaurant.getDeleted()) {
            LOGGER.error("Attempted to update deleted restaurant id {}", restaurant.getRestaurantId());
            throw new IllegalStateException("Cannot update deleted restaurant");
        }

        return restaurant;
    }

    @Transactional
    @Override
    public Restaurant update(long restaurantId, String name, RestaurantSpecialty specialty, String address, String description) {
        final Restaurant restaurant = getAndVerifyForUpdate(restaurantId);
        restaurant.setName(name);
        restaurant.setSpecialty(specialty);
        restaurant.setAddress(address);
        restaurant.setDescription(description);
        LOGGER.info("Updated name, specialty, address and description of restaurant id {}", restaurant.getRestaurantId());
        return restaurant;
    }

    @Transactional
    @Override
    public void updateImages(long restaurantId, byte[] logo, byte[] portrait1, byte[] portrait2) {
        if (logo == null && portrait1 == null && portrait2 == null)
            return;

        final Restaurant restaurant = getAndVerifyForUpdate(restaurantId);
        if (logo != null)
            imageDao.update(restaurant.getLogoId(), logo);
        if (portrait1 != null)
            imageDao.update(restaurant.getPortrait1Id(), portrait1);
        if (portrait2 != null)
            imageDao.update(restaurant.getPortrait2Id(), portrait2);

        LOGGER.info("Updated images of restaurant id {}", restaurant.getRestaurantId());
    }

    @Transactional
    @Override
    public void delete(long restaurantId) {
        restaurantDao.delete(restaurantId);
    }
}
