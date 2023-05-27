package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.persistance.ImageDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.util.PaginatedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

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
    public void delete(long restaurantId) {
        restaurantDao.delete(restaurantId);
    }
}
