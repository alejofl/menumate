import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.ProductDeletedException;
import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.persistance.RestaurantDao;
import ar.edu.itba.paw.services.ProductServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    private ProductDao productDao;

    @Mock
    private RestaurantDao restaurantDao;

    @InjectMocks
    private final ProductServiceImpl productService = new ProductServiceImpl();

    private static final long DEFAULT_PRODUCT_ID = 1L;
    private static final long DEFAULT_CATEGORY_ID = 55L;
    private static final long DEFAULT_RESTAURANT_ID = 69L;
    private static final String DEFAULT_PRODUCT_DESCRIPTION = "Default Description";
    private static final String DEFAULT_PRODUCT_NAME = "Default Name";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = new BigDecimal("9.99");
    private static final String NEW_PRODUCT_NAME = "New Name";
    private static final BigDecimal NEW_PRODUCT_PRICE = new BigDecimal("10.99");
    private static final String NEW_PRODUCT_DESCRIPTION = "New Description";
    private static final LocalDateTime DEFAULT_PROMOTION_START_DATE = LocalDateTime.now();
    private static final LocalDateTime DEFAULT_PROMOTION_END_DATE = DEFAULT_PROMOTION_START_DATE.plusDays(7);
    private static final BigDecimal DEFAULT_PROMOTION_DISCOUNT = BigDecimal.valueOf(10);

    private Category mockCategory() {
        final Category category = mock(Category.class);
        when(category.getCategoryId()).thenReturn(DEFAULT_CATEGORY_ID);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        return category;
    }

    @Test(expected = ProductNotFoundException.class)
    public void testUpdateNonExistingProduct() {
        final Restaurant restaurant = mock(Restaurant.class);
        when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, NEW_PRODUCT_PRICE, NEW_PRODUCT_DESCRIPTION);
    }

    @Test(expected = ProductDeletedException.class)
    public void testUpdateDeletedProduct() {
        final Category category = mock(Category.class);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);

        final Product product = mock(Product.class);
        when(product.getDeleted()).thenReturn(true);
        when(product.getCategoryId()).thenReturn(DEFAULT_CATEGORY_ID);
        when(product.getCategory()).thenReturn(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

        productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, NEW_PRODUCT_PRICE, DEFAULT_PRODUCT_DESCRIPTION);
    }

    @Test
    public void testUpdateProductWithSamePrice() {
        final Category category = mockCategory();

        final Product product = spy(Product.class);
        product.setProductId(DEFAULT_PRODUCT_ID);
        product.setPrice(DEFAULT_PRODUCT_PRICE);
        product.setDeleted(false);
        product.setName(DEFAULT_PRODUCT_NAME);
        product.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        product.setCategory(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));
        doAnswer(invocation -> {
            product.setName(NEW_PRODUCT_NAME);
            product.setDescription(NEW_PRODUCT_DESCRIPTION);
            return null;
        }).when(productDao).updateNameAndDescription(product, NEW_PRODUCT_NAME, NEW_PRODUCT_DESCRIPTION);


        Product ret = productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE, NEW_PRODUCT_DESCRIPTION);

        assertEquals(NEW_PRODUCT_NAME, ret.getName());
        assertEquals(NEW_PRODUCT_DESCRIPTION, ret.getDescription());
        assertEquals(DEFAULT_PRODUCT_PRICE, ret.getPrice());
    }

    @Test
    public void testUpdateProductWithDifferentPrice() {
        final Category category = mock(Category.class);
        when(category.getCategoryId()).thenReturn(DEFAULT_CATEGORY_ID);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);

        final Product existingProduct = spy(Product.class);
        existingProduct.setProductId(DEFAULT_PRODUCT_ID);
        existingProduct.setCategoryId(DEFAULT_CATEGORY_ID);
        existingProduct.setCategory(category);
        existingProduct.setName(DEFAULT_PRODUCT_NAME);
        existingProduct.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        existingProduct.setPrice(DEFAULT_PRODUCT_PRICE);
        existingProduct.setDeleted(false);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(existingProduct));

        final Product newProduct = spy(Product.class);
        newProduct.setProductId(DEFAULT_PRODUCT_ID);
        newProduct.setCategoryId(DEFAULT_CATEGORY_ID);
        newProduct.setName(DEFAULT_PRODUCT_NAME);
        newProduct.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        newProduct.setPrice(NEW_PRODUCT_PRICE);
        newProduct.setDeleted(false);

        when(productDao.create(DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_DESCRIPTION, null, NEW_PRODUCT_PRICE)).thenReturn(newProduct);

        final Product result = productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_NAME, NEW_PRODUCT_PRICE, DEFAULT_PRODUCT_DESCRIPTION);

        assertTrue(existingProduct.getDeleted());
        assertEquals(DEFAULT_CATEGORY_ID, result.getCategoryId());
        assertEquals(DEFAULT_PRODUCT_NAME, result.getName());
        assertEquals(DEFAULT_PRODUCT_DESCRIPTION, result.getDescription());
        assertEquals(NEW_PRODUCT_PRICE, result.getPrice());
        assertFalse(result.getDeleted());
    }

    @Test
    public void testCreatePromotion() {
        final Product sourceProduct = mock(Product.class);
        when(sourceProduct.getName()).thenReturn(DEFAULT_PRODUCT_NAME);
        when(sourceProduct.getDeleted()).thenReturn(false);
        when(sourceProduct.getAvailable()).thenReturn(true);

        final Category category = mock(Category.class);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        when(sourceProduct.getCategory()).thenReturn(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        final Promotion expectedPromotion = mock(Promotion.class);
        when(productDao.createPromotion(sourceProduct, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT)).thenReturn(expectedPromotion);
        when(expectedPromotion.getSource()).thenReturn(sourceProduct);
        when(expectedPromotion.getStartDate()).thenReturn(DEFAULT_PROMOTION_START_DATE);
        when(expectedPromotion.getEndDate()).thenReturn(DEFAULT_PROMOTION_END_DATE);
        when(expectedPromotion.getDiscountPercentage()).thenReturn(DEFAULT_PROMOTION_DISCOUNT);

        final Promotion result = productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);

        assertEquals(DEFAULT_PRODUCT_NAME, result.getSource().getName());
        assertEquals(DEFAULT_PROMOTION_START_DATE, result.getStartDate());
        assertEquals(DEFAULT_PROMOTION_END_DATE, result.getEndDate());
        assertEquals(DEFAULT_PROMOTION_DISCOUNT, result.getDiscountPercentage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithInvalidDiscount() {
        productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, BigDecimal.valueOf(150));
    }

    @Test(expected = ProductNotFoundException.class)
    public void testCreatePromotionWithNonExistingProduct() {
        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCreatePromotionWithDeletedProduct() {
        final Product sourceProduct = mock(Product.class);
        when(sourceProduct.getDeleted()).thenReturn(true);

        final Category category = mock(Category.class);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        when(sourceProduct.getCategory()).thenReturn(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCreatePromotionWithUnavailableProduct() {
        final Product sourceProduct = mock(Product.class);
        when(sourceProduct.getDeleted()).thenReturn(false);
        when(sourceProduct.getAvailable()).thenReturn(false);

        final Category category = mock(Category.class);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        when(sourceProduct.getCategory()).thenReturn(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithEndDateInThePast() {
        final Product sourceProduct = mock(Product.class);
        when(sourceProduct.getDeleted()).thenReturn(false);
        when(sourceProduct.getAvailable()).thenReturn(true);

        final Category category = mock(Category.class);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        when(sourceProduct.getCategory()).thenReturn(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_START_DATE.minusDays(1), DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithEndDateEqualToStartDate() {
        final Product sourceProduct = mock(Product.class);
        when(sourceProduct.getDeleted()).thenReturn(false);
        when(sourceProduct.getAvailable()).thenReturn(true);

        final Category category = mock(Category.class);
        when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        when(sourceProduct.getCategory()).thenReturn(category);

        when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_RESTAURANT_ID, DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }
}
