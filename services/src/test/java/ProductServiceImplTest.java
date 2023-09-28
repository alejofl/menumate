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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

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
        final Category category = Mockito.mock(Category.class);
        Mockito.when(category.getCategoryId()).thenReturn(DEFAULT_CATEGORY_ID);
        Mockito.when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);
        return category;
    }

    @Test(expected = ProductNotFoundException.class)
    public void testUpdateNonExistingProduct() {
        final Restaurant restaurant = Mockito.mock(Restaurant.class);
        Mockito.when(restaurantDao.getById(DEFAULT_RESTAURANT_ID)).thenReturn(Optional.of(restaurant));

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, NEW_PRODUCT_PRICE, NEW_PRODUCT_DESCRIPTION);
    }

    @Test(expected = ProductDeletedException.class)
    public void testUpdateDeletedProduct() {
        final Category category = Mockito.mock(Category.class);
        Mockito.when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);

        final Product product = Mockito.mock(Product.class);
        Mockito.when(product.getDeleted()).thenReturn(true);
        Mockito.when(product.getCategoryId()).thenReturn(DEFAULT_CATEGORY_ID);
        Mockito.when(product.getCategory()).thenReturn(category);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

        productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, NEW_PRODUCT_PRICE, DEFAULT_PRODUCT_DESCRIPTION);
    }

    @Test
    public void testUpdateProductWithSamePrice() {
        final Category category = mockCategory();

        final Product product = Mockito.spy(Product.class);
        product.setProductId(DEFAULT_PRODUCT_ID);
        product.setPrice(DEFAULT_PRODUCT_PRICE);
        product.setDeleted(false);
        product.setName(DEFAULT_PRODUCT_NAME);
        product.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        product.setCategory(category);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));
        Mockito.doAnswer(invocation -> {
            product.setName(NEW_PRODUCT_NAME);
            product.setDescription(NEW_PRODUCT_DESCRIPTION);
            return null;
        }).when(productDao).updateNameAndDescription(product, NEW_PRODUCT_NAME, NEW_PRODUCT_DESCRIPTION);


        Product ret = productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE, NEW_PRODUCT_DESCRIPTION);

        Assert.assertEquals(NEW_PRODUCT_NAME, ret.getName());
        Assert.assertEquals(NEW_PRODUCT_DESCRIPTION, ret.getDescription());
        Assert.assertEquals(DEFAULT_PRODUCT_PRICE, ret.getPrice());
    }

    @Test
    public void testUpdateProductWithDifferentPrice() {
        final Category category = Mockito.mock(Category.class);
        Mockito.when(category.getCategoryId()).thenReturn(DEFAULT_CATEGORY_ID);
        Mockito.when(category.getRestaurantId()).thenReturn(DEFAULT_RESTAURANT_ID);

        final Product existingProduct = Mockito.spy(Product.class);
        existingProduct.setProductId(DEFAULT_PRODUCT_ID);
        existingProduct.setCategoryId(DEFAULT_CATEGORY_ID);
        existingProduct.setCategory(category);
        existingProduct.setName(DEFAULT_PRODUCT_NAME);
        existingProduct.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        existingProduct.setPrice(DEFAULT_PRODUCT_PRICE);
        existingProduct.setDeleted(false);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(existingProduct));

        final Product newProduct = Mockito.spy(Product.class);
        newProduct.setProductId(DEFAULT_PRODUCT_ID);
        newProduct.setCategoryId(DEFAULT_CATEGORY_ID);
        newProduct.setName(DEFAULT_PRODUCT_NAME);
        newProduct.setDescription(DEFAULT_PRODUCT_DESCRIPTION);
        newProduct.setPrice(NEW_PRODUCT_PRICE);
        newProduct.setDeleted(false);

        Mockito.when(productDao.create(DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_DESCRIPTION, null, NEW_PRODUCT_PRICE)).thenReturn(newProduct);

        final Product result = productService.update(DEFAULT_RESTAURANT_ID, DEFAULT_CATEGORY_ID, DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_NAME, NEW_PRODUCT_PRICE, DEFAULT_PRODUCT_DESCRIPTION);

        Assert.assertTrue(existingProduct.getDeleted());
        Assert.assertEquals(DEFAULT_CATEGORY_ID, result.getCategoryId());
        Assert.assertEquals(DEFAULT_PRODUCT_NAME, result.getName());
        Assert.assertEquals(DEFAULT_PRODUCT_DESCRIPTION, result.getDescription());
        Assert.assertEquals(NEW_PRODUCT_PRICE, result.getPrice());
        Assert.assertFalse(result.getDeleted());
    }

    @Test
    public void testCreatePromotion() {
        final Product sourceProduct = Mockito.mock(Product.class);
        Mockito.when(sourceProduct.getName()).thenReturn(DEFAULT_PRODUCT_NAME);
        Mockito.when(sourceProduct.getDeleted()).thenReturn(false);
        Mockito.when(sourceProduct.getAvailable()).thenReturn(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        final Promotion expectedPromotion = Mockito.mock(Promotion.class);
        Mockito.when(productDao.createPromotion(sourceProduct, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT)).thenReturn(expectedPromotion);
        Mockito.when(expectedPromotion.getSource()).thenReturn(sourceProduct);
        Mockito.when(expectedPromotion.getStartDate()).thenReturn(DEFAULT_PROMOTION_START_DATE);
        Mockito.when(expectedPromotion.getEndDate()).thenReturn(DEFAULT_PROMOTION_END_DATE);
        //Mockito.when(expectedPromotion.getDiscountPercentage()).thenReturn((int) (DEFAULT_PROMOTION_DISCOUNT * 100));
        Mockito.when(expectedPromotion.getDiscountPercentage()).thenReturn(DEFAULT_PROMOTION_DISCOUNT);

        final Promotion result = productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);

        Assert.assertEquals(DEFAULT_PRODUCT_NAME, result.getSource().getName());
        Assert.assertEquals(DEFAULT_PROMOTION_START_DATE, result.getStartDate());
        Assert.assertEquals(DEFAULT_PROMOTION_END_DATE, result.getEndDate());
        Assert.assertEquals(DEFAULT_PROMOTION_DISCOUNT, result.getDiscountPercentage());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithInvalidDiscount() {
        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, BigDecimal.valueOf(150));
    }

    @Test(expected = ProductNotFoundException.class)
    public void testCreatePromotionWithNonExistingProduct() {
        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCreatePromotionWithDeletedProduct() {
        final Product sourceProduct = Mockito.mock(Product.class);
        Mockito.when(sourceProduct.getDeleted()).thenReturn(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCreatePromotionWithUnavailableProduct() {
        final Product sourceProduct = Mockito.mock(Product.class);
        Mockito.when(sourceProduct.getDeleted()).thenReturn(false);
        Mockito.when(sourceProduct.getAvailable()).thenReturn(false);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithEndDateInThePast() {
        final Product sourceProduct = Mockito.mock(Product.class);
        Mockito.when(sourceProduct.getDeleted()).thenReturn(false);
        Mockito.when(sourceProduct.getAvailable()).thenReturn(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_START_DATE.minusDays(1), DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithEndDateEqualToStartDate() {
        final Product sourceProduct = Mockito.mock(Product.class);
        Mockito.when(sourceProduct.getDeleted()).thenReturn(false);
        Mockito.when(sourceProduct.getAvailable()).thenReturn(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }
}
