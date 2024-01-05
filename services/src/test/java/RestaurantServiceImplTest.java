import ar.edu.itba.paw.exception.RestaurantDeletedException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.services.RestaurantServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private final RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();

    private static final long DEFAULT_RESTAURANT_ID = 1L;
    private static final String DEFAULT_RESTAURANT_NAME = "Default Name";
    private static final String DEFAULT_RESTAURANT_ADDRESS = "Default Address";
    private static final String DEFAULT_RESTAURANT_DESCRIPTION = "Default Description";
    private static final RestaurantSpecialty DEFAULT_RESTAURANT_SPECIALTY = RestaurantSpecialty.ITALIAN;
    private static final String NEW_RESTAURANT_NAME = "New Name";
    private static final String NEW_RESTAURANT_ADDRESS = "New Address";
    private static final String NEW_RESTAURANT_DESCRIPTION = "New Description";
    private static final List<RestaurantTags> DEFAULT_RESTAURANT_TAGS = new ArrayList<>(Arrays.asList(RestaurantTags.HAPPY_HOUR, RestaurantTags.CASUAL));
    private static final List<RestaurantTags> NEW_RESTAURANT_TAGS = new ArrayList<>(Arrays.asList(RestaurantTags.ROMANTIC, RestaurantTags.COSY));
    private static final RestaurantSpecialty NEW_RESTAURANT_SPECIALTY = RestaurantSpecialty.CHINESE;

    @Test
    public void testUpdateRestaurant() {
        final Restaurant existingRestaurant = spy(Restaurant.class);
        existingRestaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        existingRestaurant.setName(DEFAULT_RESTAURANT_NAME);
        existingRestaurant.setSpecialty(DEFAULT_RESTAURANT_SPECIALTY);
        existingRestaurant.setAddress(DEFAULT_RESTAURANT_ADDRESS);
        existingRestaurant.setDescription(DEFAULT_RESTAURANT_DESCRIPTION);
        existingRestaurant.setTags(DEFAULT_RESTAURANT_TAGS);
        existingRestaurant.setDeleted(false);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));

        final Restaurant updatedRestaurant = restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS);

        assertEquals(DEFAULT_RESTAURANT_ID, updatedRestaurant.getRestaurantId().intValue());
        assertEquals(NEW_RESTAURANT_NAME, updatedRestaurant.getName());
        assertEquals(NEW_RESTAURANT_SPECIALTY, updatedRestaurant.getSpecialty());
        assertEquals(NEW_RESTAURANT_ADDRESS, updatedRestaurant.getAddress());
        assertEquals(NEW_RESTAURANT_DESCRIPTION, updatedRestaurant.getDescription());
        assertEquals(NEW_RESTAURANT_TAGS, updatedRestaurant.getTags());
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testUpdateNonExistingRestaurant() {
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS);
    }

    @Test(expected = RestaurantDeletedException.class)
    public void testUpdateDeletedRestaurant() {
        final Restaurant deletedRestaurant = spy(Restaurant.class);
        deletedRestaurant.setDeleted(true);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(deletedRestaurant));
        restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS);
    }

    @Test
    public void testUpdateRestaurantWithNullOrEmptyValues() {
        final Restaurant existingRestaurant = spy(Restaurant.class);
        existingRestaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        existingRestaurant.setName(DEFAULT_RESTAURANT_NAME);
        existingRestaurant.setSpecialty(DEFAULT_RESTAURANT_SPECIALTY);
        existingRestaurant.setAddress(DEFAULT_RESTAURANT_ADDRESS);
        existingRestaurant.setDescription(DEFAULT_RESTAURANT_DESCRIPTION);
        existingRestaurant.setTags(DEFAULT_RESTAURANT_TAGS);
        existingRestaurant.setDeleted(false);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));

        final Restaurant updatedRestaurant = restaurantService.update(DEFAULT_RESTAURANT_ID, null, null, null, null, new ArrayList<>());

        assertEquals(DEFAULT_RESTAURANT_ID, updatedRestaurant.getRestaurantId().longValue());
        assertNull(updatedRestaurant.getName());
        assertNull(updatedRestaurant.getSpecialty());
        assertNull(updatedRestaurant.getAddress());
        assertNull(updatedRestaurant.getDescription());
        assertTrue(updatedRestaurant.getTags().isEmpty());
    }
}