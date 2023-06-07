import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantSpecialty;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.services.RestaurantServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class RestaurantServiceImplTest {

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private RestaurantServiceImpl restaurantService = new RestaurantServiceImpl();

    private static final long DEFAULT_RESTAURANT_ID = 1L;
    private static final String DEFAULT_RESTAURANT_NAME = "Default Name";
    private static final String DEFAULT_RESTAURANT_ADDRESS = "Default Address";
    private static final String DEFAULT_RESTAURANT_DESCRIPTION = "Default Description";
    private static final RestaurantSpecialty DEFAULT_RESTAURANT_SPECIALTY = RestaurantSpecialty.ITALIAN;
    private static final String NEW_RESTAURANT_NAME = "New Name";
    private static final String NEW_RESTAURANT_ADDRESS = "New Address";
    private static final String NEW_RESTAURANT_DESCRIPTION = "New Description";
    private static final RestaurantSpecialty NEW_RESTAURANT_SPECIALTY = RestaurantSpecialty.CHINESE;

    @Test
    public void testUpdateRestaurant() {
        final Restaurant existingRestaurant = Mockito.spy(Restaurant.class);
        existingRestaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        existingRestaurant.setName(DEFAULT_RESTAURANT_NAME);
        existingRestaurant.setSpecialty(DEFAULT_RESTAURANT_SPECIALTY);
        existingRestaurant.setAddress(DEFAULT_RESTAURANT_ADDRESS);
        existingRestaurant.setDescription(DEFAULT_RESTAURANT_DESCRIPTION);
        existingRestaurant.setDeleted(false);

        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));

        final Restaurant updatedRestaurant = restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_DESCRIPTION);

        Assert.assertEquals(DEFAULT_RESTAURANT_ID, updatedRestaurant.getRestaurantId().intValue());
        Assert.assertEquals(NEW_RESTAURANT_NAME, updatedRestaurant.getName());
        Assert.assertEquals(NEW_RESTAURANT_SPECIALTY, updatedRestaurant.getSpecialty());
        Assert.assertEquals(NEW_RESTAURANT_ADDRESS, updatedRestaurant.getAddress());
        Assert.assertEquals(NEW_RESTAURANT_DESCRIPTION, updatedRestaurant.getDescription());
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testUpdateNonExistingRestaurant() {
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.empty());
        restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_DESCRIPTION);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateDeletedRestaurant() {
        final Restaurant deletedRestaurant = Mockito.spy(Restaurant.class);
        deletedRestaurant.setDeleted(true);
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(deletedRestaurant));
        restaurantService.update(DEFAULT_RESTAURANT_ID, NEW_RESTAURANT_NAME, NEW_RESTAURANT_SPECIALTY, NEW_RESTAURANT_ADDRESS, NEW_RESTAURANT_DESCRIPTION);
    }

    @Test
    public void testUpdateRestaurantWithNullValues() {
        final Restaurant existingRestaurant = Mockito.spy(Restaurant.class);
        existingRestaurant.setRestaurantId(DEFAULT_RESTAURANT_ID);
        existingRestaurant.setName(DEFAULT_RESTAURANT_NAME);
        existingRestaurant.setSpecialty(DEFAULT_RESTAURANT_SPECIALTY);
        existingRestaurant.setAddress(DEFAULT_RESTAURANT_ADDRESS);
        existingRestaurant.setDescription(DEFAULT_RESTAURANT_DESCRIPTION);
        existingRestaurant.setDeleted(false);

        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(existingRestaurant));

        final Restaurant updatedRestaurant = restaurantService.update(DEFAULT_RESTAURANT_ID, null, null, null, null);

        Assert.assertEquals(DEFAULT_RESTAURANT_ID, updatedRestaurant.getRestaurantId().longValue());
        Assert.assertNull(updatedRestaurant.getName());
        Assert.assertNull(updatedRestaurant.getSpecialty());
        Assert.assertNull(updatedRestaurant.getAddress());
        Assert.assertNull(updatedRestaurant.getDescription());
    }
}