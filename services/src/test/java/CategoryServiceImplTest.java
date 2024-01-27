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
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
    private static final String DEFAULT_CATEGORY_NAME = "Category Name";
    private static final String NEW_CATEGORY_NAME = "New Category Name";
    private static final int DEFAULT_ORDER_NUM = 1;
    private static final int NEW_ORDER_NUM = 10;

    @Test
    public void testUpdateNameExistingCategory() {
        final Category existingCategory = spy(Category.class);
        existingCategory.setName(DEFAULT_CATEGORY_NAME);
        existingCategory.setRestaurantId(RESTAURANT_ID);
        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.of(existingCategory));

        final Category result = categoryServiceImpl.updateName(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME);

        assertEquals(NEW_CATEGORY_NAME, result.getName());
    }

    @Test(expected = CategoryNotFoundException.class)
    public void testUpdateNameNonExistingCategory() {
        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.empty());
        when(restaurantDao.getById(RESTAURANT_ID)).thenReturn(Optional.of(mock(Restaurant.class)));
        categoryServiceImpl.updateName(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME);
    }

    @Test(expected = RestaurantNotFoundException.class)
    public void testUpdateNameNonExistingCategoryNotRestaurant() {
        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.empty());
        when(restaurantDao.getById(RESTAURANT_ID)).thenReturn(Optional.empty());
        categoryServiceImpl.updateName(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME);
    }

    @Test
    public void testUpdateCategoryWithNonNullNameAndOrderNum() {
        final Category category = spy(Category.class);
        category.setRestaurantId(RESTAURANT_ID);
        category.setName(NEW_CATEGORY_NAME);
        category.setOrderNum(DEFAULT_ORDER_NUM);

        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.of(category));

        final Category updatedCategory = categoryServiceImpl.updateCategory(RESTAURANT_ID, CATEGORY_ID, NEW_CATEGORY_NAME, null);

        assertEquals(RESTAURANT_ID, updatedCategory.getRestaurantId());
        assertEquals(NEW_CATEGORY_NAME, updatedCategory.getName());
        assertEquals(DEFAULT_ORDER_NUM, updatedCategory.getOrderNum());
    }

    @Test
    public void testUpdateCategoryWithNullName() {
        final Category category = spy(Category.class);
        category.setRestaurantId(RESTAURANT_ID);
        category.setName(DEFAULT_CATEGORY_NAME);
        category.setOrderNum(DEFAULT_ORDER_NUM);

        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.of(category));

        final Category updatedCategory = categoryServiceImpl.updateCategory(RESTAURANT_ID, CATEGORY_ID, null, null);

        assertEquals(RESTAURANT_ID, updatedCategory.getRestaurantId());
        assertEquals(DEFAULT_CATEGORY_NAME, updatedCategory.getName());
        assertEquals(DEFAULT_ORDER_NUM, updatedCategory.getOrderNum());
    }

    @Test
    public void testUpdateCategoryWithNoNullOrderNumWithNullName() {
        final Category category = spy(Category.class);
        category.setRestaurantId(RESTAURANT_ID);
        category.setName(DEFAULT_CATEGORY_NAME);
        category.setOrderNum(DEFAULT_ORDER_NUM);

        when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.of(category));
        doAnswer(invocation -> {
            int newOrderNum = invocation.getArgument(1);
            category.setOrderNum(newOrderNum);
            return null;
        }).when(categoryDao).setOrder(eq(category), anyInt());

        final Category updatedCategory = categoryServiceImpl.updateCategory(RESTAURANT_ID, CATEGORY_ID, null, NEW_ORDER_NUM);

        assertEquals(RESTAURANT_ID, updatedCategory.getRestaurantId());
        assertEquals(DEFAULT_CATEGORY_NAME, updatedCategory.getName());
        assertEquals(NEW_ORDER_NUM, updatedCategory.getOrderNum());
    }
}
