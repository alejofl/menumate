import ar.edu.itba.paw.exception.InvalidUserArgumentException;
import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.services.OrderServiceImpl;
import org.junit.Assert;
import org.junit.Test;
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
    private final OrderServiceImpl orderServiceImpl = new OrderServiceImpl();

    private static final long DEFAULT_ORDER_ID = 1L;
    private static final LocalDateTime DEFAULT_DATE_TIME = LocalDateTime.now();
    private static final String DEFAULT_ADDRESS = "address";
    private static final int DEFAULT_TABLE_NUMBER = 10;

    @Test
    public void testMarkAsConfirmedValidOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setDateConfirmed(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);

        final Order result = orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CONFIRMED);
        Assert.assertNotNull(result.getDateConfirmed());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsConfirmedInvalidOrder() {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CONFIRMED);
    }

    @Test(expected = IllegalStateException.class)
    public void testMarkAsConfirmedAlreadyConfirmedOrder() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CONFIRMED);
    }

    @Test
    public void testMarkAsReadyValidOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setDateReady(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        final Order result = orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.READY);
        Assert.assertNotNull(result.getDateReady());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsReadyInvalidOrder() {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.READY);
    }

    @Test(expected = IllegalStateException.class)
    public void testMarkAsReadyAlreadyReadyOrder() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.READY);

        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.READY);
    }

    @Test
    public void testMarkAsDeliveredValidOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setDateDelivered(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.READY);

        final Order result = orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.DELIVERED);
        Assert.assertNotNull(result.getDateDelivered());
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsDeliveredInvalidOrder() {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.DELIVERED);
    }

    @Test(expected = IllegalStateException.class)
    public void testMarkAsDeliveredAlreadyDeliveredOrder() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);

        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.DELIVERED);
    }

    @Test
    public void testMarkAsCancelledConfirmedOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setDateCancelled(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CONFIRMED);

        final Order result = orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CANCELLED);
        Assert.assertNotNull(result.getDateCancelled());
    }

    @Test
    public void testMarkAsCancelledPendingOrder() {
        final Order order = Mockito.spy(Order.class);
        order.setDateCancelled(null);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.PENDING);

        final Order result = orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.REJECTED);
        Assert.assertNotNull(result.getDateCancelled());
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCantCancelDeliveredOrder() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.DELIVERED);

        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CANCELLED);
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCantCancelAlreadyCancelledOrder() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.CANCELLED);

        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CANCELLED);
    }

    @Test(expected = InvalidUserArgumentException.class)
    public void testCantCancelRejectedOrder() {
        final Order order = Mockito.mock(Order.class);
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.of(order));
        Mockito.when(order.getOrderStatus()).thenReturn(OrderStatus.REJECTED);

        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CANCELLED);
    }

    @Test(expected = OrderNotFoundException.class)
    public void testMarkAsCancelledInvalidOrder() {
        Mockito.when(orderDao.getById(DEFAULT_ORDER_ID)).thenReturn(Optional.empty());
        orderServiceImpl.advanceOrderStatus(DEFAULT_ORDER_ID, OrderStatus.CANCELLED);
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

        Assert.assertEquals(DEFAULT_TABLE_NUMBER, order.getTableNumber().intValue());
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
