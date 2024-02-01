import ar.edu.itba.paw.exception.RestaurantDeletedException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.OrderService;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantDao restaurantDao;

    @Mock
    private EmailService emailService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private final RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();

    private static final long DEFAULT_RESTAURANT_ID = 1L;
    private static final String DEFAULT_RESTAURANT_NAME = "Default Name";
    private static final String DEFAULT_RESTAURANT_ADDRESS = "Default Address";
    private static final int DEFAULT_RESTAURANT_MAX_TABLES = 200;
    private static final String DEFAULT_RESTAURANT_DESCRIPTION = "Default Description";
    private static final RestaurantSpecialty DEFAULT_RESTAURANT_SPECIALTY = RestaurantSpecialty.ITALIAN;
    private static final Long DEFAULT_LOGO_ID = 51L;
    private static final Long DEFAULT_PORTRAIT_ID_1 = 80L;
    private static final Long DEFAULT_PORTRAIT_ID_2 = 70L;
    private static final String NEW_RESTAURANT_NAME = "New Name";
    private static final String NEW_RESTAURANT_ADDRESS = "New Address";
    private static final String NEW_RESTAURANT_DESCRIPTION = "New Description";
    private static final int NEW_RESTAURANT_MAX_TABLES = 60;
    private static final List<RestaurantTags> DEFAULT_RESTAURANT_TAGS = new ArrayList<>(Arrays.asList(RestaurantTags.HAPPY_HOUR, RestaurantTags.CASUAL));
    private static final List<RestaurantTags> NEW_RESTAURANT_TAGS = new ArrayList<>(Arrays.asList(RestaurantTags.ROMANTIC, RestaurantTags.COSY));
    private static final RestaurantSpecialty NEW_RESTAURANT_SPECIALTY = RestaurantSpecialty.CHINESE;
    private static final Long NEW_LOGO_ID = 100L;
    private static final Long NEW_PORTRAIT_ID_1 = 150L;
    private static final Long NEW_PORTRAIT_ID_2 = 200L;

    @Test
    public void testUpdateRestaurant() {
        final Restaurant existingRestaurant = spy(Restaurant.class);
        existingRestaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        existingRestaurant.setName(DEFAULT_RESTAURANT_NAME);
        existingRestaurant.setSpecialty(DEFAULT_RESTAURANT_SPECIALTY);
        existingRestaurant.setAddress(DEFAULT_RESTAURANT_ADDRESS);
        existingRestaurant.setMaxTables(DEFAULT_RESTAURANT_MAX_TABLES);
        existingRestaurant.setDescription(DEFAULT_RESTAURANT_DESCRIPTION);
        existingRestaurant.setTags(DEFAULT_RESTAURANT_TAGS);
        existingRestaurant.setLogoId(DEFAULT_LOGO_ID);
        existingRestaurant.setPortrait1Id(DEFAULT_PORTRAIT_ID_1);
        existingRestaurant.setPortrait2Id(DEFAULT_PORTRAIT_ID_2);
        existingRestaurant.setDeleted(false);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));

        final Restaurant updatedRestaurant = restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_MAX_TABLES, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS, NEW_LOGO_ID, NEW_PORTRAIT_ID_1, NEW_PORTRAIT_ID_2);

        assertEquals(DEFAULT_RESTAURANT_ID, updatedRestaurant.getRestaurantId().intValue());
        assertEquals(NEW_RESTAURANT_NAME, updatedRestaurant.getName());
        assertEquals(NEW_RESTAURANT_SPECIALTY, updatedRestaurant.getSpecialty());
        assertEquals(NEW_RESTAURANT_ADDRESS, updatedRestaurant.getAddress());
        assertEquals(NEW_RESTAURANT_DESCRIPTION, updatedRestaurant.getDescription());
        assertEquals(NEW_RESTAURANT_TAGS, updatedRestaurant.getTags());
        assertEquals(NEW_RESTAURANT_MAX_TABLES, updatedRestaurant.getMaxTables());
        assertEquals(NEW_LOGO_ID, updatedRestaurant.getLogoId());
        assertEquals(NEW_PORTRAIT_ID_1, updatedRestaurant.getPortrait1Id());
        assertEquals(NEW_PORTRAIT_ID_2, updatedRestaurant.getPortrait2Id());
    }

    @Test
    public void tesUpdateNoImageFromRestaurant() {
        final Restaurant existingRestaurant = spy(Restaurant.class);
        existingRestaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        existingRestaurant.setName(DEFAULT_RESTAURANT_NAME);
        existingRestaurant.setSpecialty(DEFAULT_RESTAURANT_SPECIALTY);
        existingRestaurant.setAddress(DEFAULT_RESTAURANT_ADDRESS);
        existingRestaurant.setMaxTables(DEFAULT_RESTAURANT_MAX_TABLES);
        existingRestaurant.setDescription(DEFAULT_RESTAURANT_DESCRIPTION);
        existingRestaurant.setTags(DEFAULT_RESTAURANT_TAGS);
        existingRestaurant.setLogoId(DEFAULT_LOGO_ID);
        existingRestaurant.setPortrait1Id(DEFAULT_PORTRAIT_ID_1);
        existingRestaurant.setPortrait2Id(DEFAULT_PORTRAIT_ID_2);
        existingRestaurant.setDeleted(false);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));

        final Restaurant updatedRestaurant = restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_MAX_TABLES, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS, null, null, null);

        assertEquals(DEFAULT_RESTAURANT_ID, updatedRestaurant.getRestaurantId().intValue());
        assertEquals(NEW_RESTAURANT_NAME, updatedRestaurant.getName());
        assertEquals(NEW_RESTAURANT_SPECIALTY, updatedRestaurant.getSpecialty());
        assertEquals(NEW_RESTAURANT_ADDRESS, updatedRestaurant.getAddress());
        assertEquals(NEW_RESTAURANT_DESCRIPTION, updatedRestaurant.getDescription());
        assertEquals(NEW_RESTAURANT_TAGS, updatedRestaurant.getTags());
        assertEquals(NEW_RESTAURANT_MAX_TABLES, updatedRestaurant.getMaxTables());
        assertEquals(DEFAULT_LOGO_ID, updatedRestaurant.getLogoId());
        assertEquals(DEFAULT_PORTRAIT_ID_1, updatedRestaurant.getPortrait1Id());
        assertEquals(DEFAULT_PORTRAIT_ID_2, updatedRestaurant.getPortrait2Id());
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testUpdateNonExistingRestaurant() {
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_MAX_TABLES, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS, NEW_LOGO_ID, NEW_PORTRAIT_ID_1, NEW_PORTRAIT_ID_2);
    }

    @Test(expected = RestaurantDeletedException.class)
    public void testUpdateDeletedRestaurant() {
        final Restaurant deletedRestaurant = spy(Restaurant.class);
        deletedRestaurant.setDeleted(true);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(deletedRestaurant));
        restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_MAX_TABLES, NEW_RESTAURANT_DESCRIPTION, NEW_RESTAURANT_TAGS, NEW_LOGO_ID, NEW_PORTRAIT_ID_1, NEW_PORTRAIT_ID_2);
    }

    @Test
    public void testHandleActivationFromTrueToFalse() {
        final Restaurant restaurant = spy(Restaurant.class);
        restaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        restaurant.setIsActive(true);
        restaurant.setDeleted(false);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        restaurantService.handleActivation(DEFAULT_RESTAURANT_ID, false);

        assertFalse(restaurant.getIsActive());
    }

    @Test
    public void testHandleActivationFromFalseToTrue() {
        final Restaurant restaurant = spy(Restaurant.class);
        restaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        restaurant.setIsActive(false);
        restaurant.setDeleted(false);

        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        restaurantService.handleActivation(DEFAULT_RESTAURANT_ID, true);

        assertTrue(restaurant.getIsActive());
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testHandleActivationNoRestaurant() {
        final Restaurant restaurant = mock(Restaurant.class);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());

        restaurantService.handleActivation(DEFAULT_RESTAURANT_ID, false);

        assertFalse(restaurant.getIsActive());
    }

    @Test(expected = RestaurantDeletedException.class)
    public void testHandleActivationDeletedRestaurant() {
        final Restaurant restaurant = spy(Restaurant.class);
        restaurant.setDeleted(true);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        restaurantService.handleActivation(DEFAULT_RESTAURANT_ID, false);

        assertFalse(restaurant.getIsActive());
    }
}