import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.services.OrderServiceImpl;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @Mock
    private OrderDao orderDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderServiceImpl orderServiceImpl = new OrderServiceImpl();

    private static final long DEFAULT_ORDER_ID = 1L;
    private static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.now();
    private static final String DEFAULT_ADDRESS = "address";
    private static final int DEFAULT_TABLE_NUMBER = 10;

    @Before
    public void setUp() {
    }

    @Test
    public void testMarkAsConfirmedValidOrder() throws OrderNotFoundException {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);

        final Order result = orderServiceImpl.markAsConfirmed(DEFAULT_ORDER_ID);
        Assert.assertNotNull(result.getDateConfirmed());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsConfirmedInvalidOrder() throws OrderNotFoundException {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.markAsConfirmed(DEFAULT_ORDER_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testMarkAsConfirmedAlreadyConfirmedOrder() throws OrderNotFoundException {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        orderServiceImpl.markAsConfirmed(DEFAULT_ORDER_ID);
    }

    @Test
    public void testMarkAsReadyValidOrder() throws OrderNotFoundException {
        final Order order = Mockito.spy(Order.class);
        order.setDateReady(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        final Order result = orderServiceImpl.markAsReady(DEFAULT_ORDER_ID);
        Assert.assertNotNull(result.getDateReady());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsReadyInvalidOrder() throws OrderNotFoundException {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.markAsReady(DEFAULT_ORDER_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testMarkAsReadyAlreadyReadyOrder() throws OrderNotFoundException {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.READY);

        orderServiceImpl.markAsReady(DEFAULT_ORDER_ID);
    }

    @Test
    public void testMarkAsDeliveredValidOrder() throws OrderNotFoundException {
        final Order order = Mockito.spy(Order.class);
        order.setDateDelivered(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.READY);

        final Order result = orderServiceImpl.markAsDelivered(DEFAULT_ORDER_ID);
        Assert.assertNotNull(result.getDateDelivered());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsDeliveredInvalidOrder() throws OrderNotFoundException {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.markAsDelivered(DEFAULT_ORDER_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testMarkAsDeliveredAlreadyDeliveredOrder() throws OrderNotFoundException {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);

        orderServiceImpl.markAsDelivered(DEFAULT_ORDER_ID);
    }

    @Test
    public void testMarkAsCancelledConfirmedOrder() throws OrderNotFoundException {
        final Order order = Mockito.spy(Order.class);
        order.setDateCancelled(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        final Order result = orderServiceImpl.markAsCancelled(DEFAULT_ORDER_ID);
        Assert.assertNotNull(result.getDateCancelled());
    }

    @Test
    public void testMarkAsCancelledPendingOrder() throws OrderNotFoundException {
        final Order order = Mockito.spy(Order.class);
        order.setDateCancelled(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);

        final Order result = orderServiceImpl.markAsCancelled(DEFAULT_ORDER_ID);
        Assert.assertNotNull(result.getDateCancelled());
    }

    @Test(expected = IllegalStateException.class)
    public void testCantCancelDeliveredOrder() throws OrderNotFoundException {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);

        orderServiceImpl.markAsCancelled(DEFAULT_ORDER_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testCantCancelAlreadyCancelledOrder() throws OrderNotFoundException {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELLED);

        orderServiceImpl.markAsCancelled(DEFAULT_ORDER_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testCantCancelRejectedOrder() throws OrderNotFoundException {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.REJECTED);

        orderServiceImpl.markAsCancelled(DEFAULT_ORDER_ID);
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsCancelledInvalidOrder() throws OrderNotFoundException {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.markAsCancelled(DEFAULT_ORDER_ID);
    }

    @Test
    public void testSetOrderStatusPending() {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);
        order.setDateCancelled(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.PENDING);

        Assert.assertNull(order.getDateConfirmed());
        Assert.assertNull(order.getDateReady());
        Assert.assertNull(order.getDateDelivered());
        Assert.assertNull(order.getDateCancelled());
    }

    @Test
    public void testSetOrderStatusConfirmed() {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);
        order.setDateCancelled(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CONFIRMED);

        Assert.assertNull(order.getDateDelivered());
        Assert.assertNull(order.getDateReady());
        Assert.assertNull(order.getDateCancelled());
        Assert.assertNotNull(order.getDateConfirmed());
    }

    @Test
    public void testSetOrderStatusReady() {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);
        order.setDateCancelled(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.READY);

        Assert.assertNull(order.getDateDelivered());
        Assert.assertNull(order.getDateCancelled());
        Assert.assertNotNull(order.getDateConfirmed());
        Assert.assertNotNull(order.getDateReady());
    }

    @Test
    public void testSetOrderStatusDelivered() {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);
        order.setDateCancelled(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.DELIVERED);

        Assert.assertNull(order.getDateCancelled());
        Assert.assertNotNull(order.getDateReady());
        Assert.assertNotNull(order.getDateConfirmed());
        Assert.assertNotNull(order.getDateDelivered());
    }

    @Test
    public void testSetOrderStatusCancelled() {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);
        order.setDateCancelled(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CANCELLED);

        Assert.assertNull(order.getDateDelivered());
        Assert.assertNotNull(order.getDateReady());
        Assert.assertNotNull(order.getDateConfirmed());
        Assert.assertNotNull(order.getDateCancelled());
    }

    @Test
    public void testSetOrderStatusRejected() {
        final Order order = Mockito.spy(Order.class);
        order.setDateCancelled(DEFAULT_DATE_TIME);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.REJECTED);

        Assert.assertNull(order.getDateConfirmed());
        Assert.assertNull(order.getDateReady());
        Assert.assertNull(order.getDateDelivered());
        Assert.assertEquals(DEFAULT_DATE_TIME, order.getDateCancelled());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testSetInvalidOrder() {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());

        orderServiceImpl.setOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CONFIRMED);
    }

    @Test
    public void testUpdateAddressValidAddress() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DELIVERY);
        order.setDateOrdered(DEFAULT_DATE_TIME);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateAddress(DEFAULT_ORDER_ID, DEFAULT_ADDRESS);

        Assert.assertEquals(DEFAULT_ADDRESS, order.getAddress());
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateAddressTakeAway() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(order.getOrderType()).thenReturn(OrderType.TAKEAWAY);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateAddress(DEFAULT_ORDER_ID, DEFAULT_ADDRESS);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateAddressDineIn() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(order.getOrderType()).thenReturn(OrderType.DINE_IN);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateAddress(DEFAULT_ORDER_ID, DEFAULT_ADDRESS);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateAddressClosedOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DELIVERY);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateAddress(DEFAULT_ORDER_ID, DEFAULT_ADDRESS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateAddressNullAddress() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DELIVERY);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateAddress(DEFAULT_ORDER_ID, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateAddressBlankAddress() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DELIVERY);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateAddress(DEFAULT_ORDER_ID, "");
    }

    @Test
    public void testUpdateTableNumberValidTableNumber() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DINE_IN);
        order.setDateOrdered(DEFAULT_DATE_TIME);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateTableNumber(DEFAULT_ORDER_ID, DEFAULT_TABLE_NUMBER);

        Assert.assertEquals(DEFAULT_TABLE_NUMBER, order.getTableNumber());
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateTableNumberDeliveryOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DELIVERY);
        order.setDateOrdered(DEFAULT_DATE_TIME);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateTableNumber(DEFAULT_ORDER_ID, DEFAULT_TABLE_NUMBER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateTableNumberTakeawayOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.TAKEAWAY);
        order.setDateOrdered(DEFAULT_DATE_TIME);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateTableNumber(DEFAULT_ORDER_ID, DEFAULT_TABLE_NUMBER);
    }

    @Test(expected = IllegalStateException.class)
    public void testUpdateTableNumberClosedOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setOrderType(OrderType.DINE_IN);
        order.setDateOrdered(DEFAULT_DATE_TIME);
        order.setDateConfirmed(DEFAULT_DATE_TIME);
        order.setDateReady(DEFAULT_DATE_TIME);
        order.setDateDelivered(DEFAULT_DATE_TIME);

        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));

        orderServiceImpl.updateTableNumber(DEFAULT_ORDER_ID, DEFAULT_TABLE_NUMBER);
    }
}
