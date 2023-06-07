import ar.edu.itba.paw.exception.ProductNotFoundException;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.Promotion;
import ar.edu.itba.paw.persistance.ProductDao;
import ar.edu.itba.paw.services.ProductServiceImpl;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import org.junit.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService = new ProductServiceImpl();

    private static final long DEFAULT_PRODUCT_ID = 1L;
    private static final String DEFAULT_PRODUCT_DESCRIPTION = "Default Description";
    private static final String DEFAULT_PRODUCT_NAME = "Default Name";
    private static final BigDecimal DEFAULT_PRODUCT_PRICE = BigDecimal.valueOf(9.99);
    private static final String NEW_PRODUCT_NAME = "New Name";
    private static final BigDecimal NEW_PRODUCT_PRICE = BigDecimal.valueOf(10.99);
    private static final String NEW_PRODUCT_DESCRIPTION = "New Description";
    private static final LocalDateTime DEFAULT_PROMOTION_START_DATE = LocalDateTime.now();
    private static final LocalDateTime DEFAULT_PROMOTION_END_DATE = DEFAULT_PROMOTION_START_DATE.plusDays(7);
    private static final float DEFAULT_PROMOTION_DISCOUNT = 0.1f;


    @Test(expected = ProductNotFoundException.class)
    public void testUpdateNonExistingProduct() {
        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        productService.update(DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, NEW_PRODUCT_PRICE, NEW_PRODUCT_DESCRIPTION);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateDeletedProduct() {
        Product product = Mockito.spy(Product.class);
        product.setDeleted(true);
        product.setProductId(DEFAULT_PRODUCT_ID);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

        productService.update(DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, NEW_PRODUCT_PRICE, DEFAULT_PRODUCT_DESCRIPTION);
    }

    @Test
    public void testUpdateProductWithSamePrice() {
        Product product = Mockito.mock(Product.class);
        Mockito.when(product.getPrice()).thenReturn(DEFAULT_PRODUCT_PRICE);
        Mockito.when(product.getDeleted()).thenReturn(false);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(product));

        productService.update(DEFAULT_PRODUCT_ID, NEW_PRODUCT_NAME, DEFAULT_PRODUCT_PRICE, NEW_PRODUCT_DESCRIPTION);

        Mockito.verify(productDao).updateNameAndDescription(Mockito.eq(product), Mockito.eq(NEW_PRODUCT_NAME), Mockito.eq(NEW_PRODUCT_DESCRIPTION));
    }

    @Test
    public void testUpdateProductWithDifferentPrice() {
        Product existingProduct = Mockito.spy(Product.class);
        existingProduct.setProductId(DEFAULT_PRODUCT_ID);
        existingProduct.setPrice(DEFAULT_PRODUCT_PRICE);
        existingProduct.setDeleted(false);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(existingProduct));

        Product newProduct = Mockito.spy(Product.class);
        newProduct.setProductId(DEFAULT_PRODUCT_ID);
        newProduct.setPrice(NEW_PRODUCT_PRICE);
        newProduct.setDeleted(false);

        Mockito.when(productDao.create(existingProduct.getCategoryId(), DEFAULT_PRODUCT_NAME, DEFAULT_PRODUCT_DESCRIPTION, existingProduct.getImageId(), NEW_PRODUCT_PRICE)).thenReturn(newProduct);

        Product result = productService.update(DEFAULT_PRODUCT_ID, DEFAULT_PRODUCT_NAME, NEW_PRODUCT_PRICE, DEFAULT_PRODUCT_DESCRIPTION);

        Assert.assertTrue(existingProduct.getDeleted());
        Assert.assertEquals(NEW_PRODUCT_PRICE, newProduct.getPrice());
        Assert.assertSame(newProduct, result);
    }

    @Test
    public void testCreatePromotion() {
        Product sourceProduct = Mockito.spy(Product.class);
        sourceProduct.setProductId(DEFAULT_PRODUCT_ID);
        sourceProduct.setDeleted(false);
        sourceProduct.setAvailable(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        Promotion expectedPromotion = Mockito.spy(Promotion.class);
        Mockito.when(productDao.createPromotion(sourceProduct, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT)).thenReturn(expectedPromotion);

        Promotion result = productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);

        Mockito.verify(productDao).createPromotion(sourceProduct, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
        Assert.assertEquals(expectedPromotion, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithInvalidDiscount() {
        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, 1.5f);
    }

    @Test(expected = ProductNotFoundException.class)
    public void testCreatePromotionWithNonExistingProduct() {
        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.empty());
        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreatePromotionWithDeletedProduct() {
        Product sourceProduct = Mockito.spy(Product.class);
        sourceProduct.setProductId(DEFAULT_PRODUCT_ID);
        sourceProduct.setDeleted(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreatePromotionWithUnavailableProduct() {
        Product sourceProduct = Mockito.spy(Product.class);
        sourceProduct.setProductId(DEFAULT_PRODUCT_ID);
        sourceProduct.setDeleted(false);
        sourceProduct.setAvailable(false);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_END_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithEndDateInThePast() {
        Product sourceProduct = Mockito.spy(Product.class);
        sourceProduct.setProductId(DEFAULT_PRODUCT_ID);
        sourceProduct.setDeleted(false);
        sourceProduct.setAvailable(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_START_DATE.minusDays(1), DEFAULT_PROMOTION_DISCOUNT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePromotionWithEndDateEqualToStartDate() {
        Product sourceProduct = Mockito.spy(Product.class);
        sourceProduct.setProductId(DEFAULT_PRODUCT_ID);
        sourceProduct.setDeleted(false);
        sourceProduct.setAvailable(true);

        Mockito.when(productDao.getById(DEFAULT_PRODUCT_ID)).thenReturn(Optional.of(sourceProduct));

        productService.createPromotion(DEFAULT_PRODUCT_ID, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_START_DATE, DEFAULT_PROMOTION_DISCOUNT);
    }
}
