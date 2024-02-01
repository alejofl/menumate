package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.RestaurantDeletedException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.OrderService;
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
    private static final int DAYS = 15;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private ImageDao imageDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderService orderService;

    @Transactional
    @Override
    public Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, Long logoId, Long portrait1Id, Long portrait2Id, boolean isActive, List<RestaurantTags> tags) {
        final Optional<Image> logo = logoId == null ? Optional.empty() : imageDao.getById(logoId);
        final Optional<Image> portrait1 = portrait1Id == null ? Optional.empty() : imageDao.getById(portrait1Id);
        final Optional<Image> portrait2 = portrait2Id == null ? Optional.empty() : imageDao.getById(portrait2Id);
        return restaurantDao.create(name, email, specialty, ownerUserId, address, description, maxTables, logo.map(Image::getImageId).orElse(null), portrait1.map(Image::getImageId).orElse(null), portrait2.map(Image::getImageId).orElse(null), isActive, tags);
    }

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return restaurantDao.getById(restaurantId);
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
    public Optional<RestaurantDetails> getRestaurantDetails(long restaurantId) {
        return restaurantDao.getRestaurantDetails(restaurantId);
    }

    @Override
    public Optional<Duration> getAverageOrderCompletionTime(long restaurantId, OrderType orderType) {
        final LocalDateTime since = LocalDateTime.now().minusDays(DAYS);
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
    public Restaurant update(long restaurantId, String name, RestaurantSpecialty specialty, String address, int maxTables, String description, List<RestaurantTags> tags, Long logoId, Long portrait1Id, Long portrait2Id) {
        final Restaurant restaurant = getAndVerifyForUpdate(restaurantId);
        restaurant.setName(name);
        restaurant.setSpecialty(specialty);
        restaurant.setAddress(address);
        restaurant.setMaxTables(maxTables);
        restaurant.setDescription(description);
        restaurant.getTags().clear();
        restaurant.getTags().addAll(tags);

        Optional.ofNullable(logoId).ifPresent(restaurant::setLogoId);
        Optional.ofNullable(portrait1Id).ifPresent(restaurant::setPortrait1Id);
        Optional.ofNullable(portrait2Id).ifPresent(restaurant::setPortrait2Id);
        LOGGER.info("Updated name, specialty, address, description, max tables, logo, portraits and tags of restaurant id {}", restaurant.getRestaurantId());
        return restaurant;
    }

    @Transactional
    @Override
    public void delete(long restaurantId) {
        restaurantDao.delete(restaurantId);
        orderService.cancelNonDeliveredOrders(restaurantId);
    }

    @Transactional
    @Override
    public void handleActivation(long restaurantId, boolean activate) {
        final Restaurant restaurant = getAndVerifyForUpdate(restaurantId);
        if (restaurant.getIsActive() == activate) {
            return;
        }

        restaurant.setIsActive(activate);
        LOGGER.info("Updated restaurant {} isActive field to {}", restaurantId, activate);
        if (!activate) {
            emailService.sendRestaurantDeactivationEmail(restaurant);
        }
    }
}
