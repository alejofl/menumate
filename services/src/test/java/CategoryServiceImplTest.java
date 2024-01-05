import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.services.CategoryServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private final CategoryServiceImpl categoryServiceImpl = new CategoryServiceImpl();

    private static final long CATEGORY_ID = 1;
    private static final long RESTAURANT_ID = 1;
    private static final String ORIGINAL_CATEGORY_NAME = "Category Name";
    private static final String NEW_CATEGORY_NAME = "New Category Name";

    @Test
    public void testUpdateNameExistingCategory() {
        final Category existingCategory = Mockito.spy(Category.class);
        existingCategory.setName(ORIGINAL_CATEGORY_NAME);
        existingCategory.setRestaurantId(RESTAURANT_ID);
        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.of(existingCategory));

        final Category result = categoryServiceImpl.updateName(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME);

        assertEquals(NEW_CATEGORY_NAME, result.getName());
    }

    @Test(expected = CategoryNotFoundException.class)
    public void testUpdateNameNonExistingCategory() {
        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.empty());
        when(restaurantDao.getById(RESTAURANT_ID)).thenReturn(Optional.of(Mockito.mock(Restaurant.class)));
        categoryServiceImpl.updateName(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME);
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testUpdateNameNonExistingCategoryNotRestaurant() {
        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.empty());
        when(restaurantDao.getById(RESTAURANT_ID)).thenReturn(Optional.empty());
        categoryServiceImpl.updateName(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME);
    }
}
