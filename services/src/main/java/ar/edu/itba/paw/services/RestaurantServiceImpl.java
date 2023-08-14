package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.RestaurantDeletedException;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantServiceImpl.class);

    static final LocalDateTime MINIMUM_DATETIME = LocalDateTime.of(1970, 1, 1, 0, 0);

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ImageDao imageDao;

    @Transactional
    @Override
    public Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, byte[] logo, byte[] portrait1, byte[] portrait2, boolean isActive, List<RestaurantTags> tags) {
        final long logoId = imageDao.create(logo);
        final long portrait1Id = imageDao.create(portrait1);
        final long portrait2Id = imageDao.create(portrait2);

        return restaurantDao.create(name, email, specialty, ownerUserId, address, description, maxTables, logoId, portrait1Id, portrait2Id, isActive, tags);
    }

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        final Optional<Restaurant> restaurant = restaurantDao.getById(restaurantId);
        if (restaurant.isPresent() && restaurant.get().getDeleted())
            throw new RestaurantDeletedException();
        return restaurant;
    }

    @Override
    public PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties) {
        return restaurantDao.search(query, pageNumber, pageSize, orderBy, descending, tags, specialties);
    }

    @Override
    public List<Promotion> getActivePromotions(long restaurantId) {
        return restaurantDao.getActivePromotions(restaurantId);
    }

    @Override
    public List<Promotion> getLivingPromotions(long restaurantId) {
        return restaurantDao.getLivingPromotions(restaurantId);
    }

    @Override
    public Optional<Duration> getAverageOrderCompletionTime(long restaurantId, OrderType orderType) {
        LocalDateTime since = LocalDateTime.now().minusDays(15);
        return restaurantDao.getAverageOrderCompletionTime(restaurantId, orderType, since);
    }

    private Restaurant getAndVerifyForUpdate(long restaurantId) {
        final Restaurant restaurant = restaurantDao.getById(restaurantId).orElse(null);
        if (restaurant == null) {
            LOGGER.error("Attempted to update nonexisting restaurant id {}", restaurantId);
            throw new RestaurantNotFoundException();
        }

        if (restaurant.getDeleted()) {
            LOGGER.error("Attempted to update deleted restaurant id {}", restaurant.getRestaurantId());
            throw new RestaurantDeletedException();
        }

        return restaurant;
    }

    @Transactional
    @Override
    public Restaurant update(long restaurantId, String name, RestaurantSpecialty specialty, String address, String description, List<RestaurantTags> tags) {
        final Restaurant restaurant = getAndVerifyForUpdate(restaurantId);
        restaurant.setName(name);
        restaurant.setSpecialty(specialty);
        restaurant.setAddress(address);
        restaurant.setDescription(description);
        restaurant.getTags().clear();
        restaurant.getTags().addAll(tags);
        LOGGER.info("Updated name, specialty, address, description and tags of restaurant id {}", restaurant.getRestaurantId());
        return restaurant;
    }

    @Transactional
    @Override
    public void updateImages(long restaurantId, byte[] logo, byte[] portrait1, byte[] portrait2) {
        if ((logo == null || logo.length == 0) && (portrait1 == null || portrait1.length == 0) && (portrait2 == null || portrait2.length == 0))
            return;

        final Restaurant restaurant = getAndVerifyForUpdate(restaurantId);
        if (logo != null && logo.length != 0)
            imageDao.update(restaurant.getLogoId(), logo);
        if (portrait1 != null && portrait1.length != 0)
            imageDao.update(restaurant.getPortrait1Id(), portrait1);
        if (portrait2 != null && portrait2.length != 0)
            imageDao.update(restaurant.getPortrait2Id(), portrait2);

        LOGGER.info("Updated images of restaurant id {}", restaurant.getRestaurantId());
    }

    @Transactional
    @Override
    public void delete(long restaurantId) {
        restaurantDao.delete(restaurantId);
    }
}
