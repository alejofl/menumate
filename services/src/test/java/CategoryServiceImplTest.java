import ar.edu.itba.paw.exception.CategoryNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.persistance.CategoryDao;
import ar.edu.itba.paw.services.CategoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private final CategoryServiceImpl categoryServiceImpl = new CategoryServiceImpl();

    private static final long CATEGORY_ID = 1L;
    private static final String ORIGINAL_CATEGORY_NAME = "Category Name";
    private static final String NEW_CATEGORY_NAME = "New Category Name";

    @Test
    public void testUpdateNameExistingCategory() {
        final Category existingCategory = Mockito.spy(Category.class);
        existingCategory.setName(ORIGINAL_CATEGORY_NAME);
        Mockito.when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.of(existingCategory));

        final Category result = categoryServiceImpl.updateName(CATEGORY_ID, NEW_CATEGORY_NAME);

        Assert.assertEquals(NEW_CATEGORY_NAME, result.getName());
    }

    @Test(expected = CategoryNotFoundException.class)
    public void testUpdateNameNonExistingCategory() {
        Mockito.when(categoryDao.getById(CATEGORY_ID)).thenReturn(Optional.empty());
        categoryServiceImpl.updateName(CATEGORY_ID, NEW_CATEGORY_NAME);
    }

}
