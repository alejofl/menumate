package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.util.PaginatedResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RestaurantJpaDao implements RestaurantDao {

    private final static Logger LOGGER = LoggerFactory.getLogger(RestaurantJpaDao.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Restaurant> getById(long restaurantId) {
        return Optional.ofNullable(em.find(Restaurant.class, restaurantId));
    }

    @Override
    public Restaurant create(String name, String email, RestaurantSpecialty specialty, long ownerUserId, String address, String description, int maxTables, Long logoId, Long portrait1Id, Long portrait2Id, boolean isActive, List<RestaurantTags> tags) {
        final User owner = em.getReference(User.class, ownerUserId);
        final Restaurant restaurant = new Restaurant(name, email, specialty, owner, address, description, maxTables, logoId, portrait1Id, portrait2Id, isActive, tags);
        em.persist(restaurant);
        LOGGER.info("Created restaurant with ID {}", restaurant.getRestaurantId());
        return restaurant;
    }

    @Override
    public PaginatedResult<RestaurantDetails> search(String query, int pageNumber, int pageSize, RestaurantOrderBy orderBy, boolean descending, List<RestaurantTags> tags, List<RestaurantSpecialty> specialties) {
        // TODO: Implement. This is just a placeholder.
        List<Restaurant> restaurants = new ArrayList<>();
        for (int i = 0; i < pageSize; i++)
            getById(i).ifPresent(r -> restaurants.add(r));
        List<RestaurantDetails> pedro = restaurants.stream().map(restaurant -> new RestaurantDetails(restaurant, 1, 1, 1)).collect(Collectors.toList());
        return new PaginatedResult<>(pedro, pageNumber, pageSize, restaurants.size());
    }

    @Override
    public void delete(Restaurant restaurant) {
        em.remove(restaurant);
    }

    @Override
    public void delete(long restaurantId) {
        em.remove(em.getReference(Restaurant.class, restaurantId));
    }
}
