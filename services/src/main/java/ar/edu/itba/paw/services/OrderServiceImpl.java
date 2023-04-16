package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    private long getUserId(String name, String email){
        return userService.createIfNotExists(name, email).getUserId();
    }

    @Override
    public Order createDelivery(long restaurantId, long userId, String address, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, userId, address, items);
    }

    @Override
    public Order createDelivery(long restaurantId, String name, String email, String address, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, getUserId(name, email), address, items);
    }

    @Override
    public Order createDineIn(long restaurantId, long userId, int tableNumber, List<OrderItem> items) {
        return orderDao.create(OrderType.DINE_IN, restaurantId, userId, tableNumber, items);
    }

    @Override
    public Order createDineIn(long restaurantId, String name, String email, int tableNumber, List<OrderItem> items) {
        return orderDao.create(OrderType.DINE_IN, restaurantId, getUserId(name, email), tableNumber, items);
    }

    @Override
    public Order createTakeAway(long restaurantId, long userId, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, userId, items);
    }

    @Override
    public Order createTakeAway(long restaurantId, String name, String email, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, getUserId(name, email), items);
    }


    @Override
    public OrderItem createOrderItem(long productId, int lineNumber, int quantity, String comment) {
        Product product = productService.getById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product id"));
        return orderDao.createOrderItem(product, lineNumber, quantity, comment);
    }

    @Override
    public Optional<Order> getById(long orderId) {
        return orderDao.getById(orderId);
    }

    @Override
    public List<Order> getByUser(long userId, long restaurantId) {
        return orderDao.getByUser(userId, restaurantId);
    }

    @Override
    public List<Order> getByRestaurant(long restaurantId) {
        return orderDao.getByRestaurant(restaurantId);
    }

    @Override
    public List<Order> getOrderedBetweenDates(long restaurantId, LocalDateTime start, LocalDateTime end) {
        return orderDao.getByRestaurantOrderedBetweenDates(restaurantId, start, end);
    }

    @Override
    public List<Order> getByAddress(long restaurantId, String address) {
        return orderDao.getByRestaurantAndAddress(restaurantId, address);
    }

    @Override
    public List<Order> getByTableNumber(long restaurantId, int tableNumber) {
        return orderDao.getByRestaurantAndTableNumber(restaurantId, tableNumber);
    }

    @Override
    public List<Order> getByOrderTypeAndRestaurant(OrderType orderType, long restaurantId) {
        return orderDao.getByOrderTypeAndRestaurant(orderType, restaurantId);
    }

    @Override
    public boolean updateAddress(long orderId, String address) {
        return orderDao.updateAddress(orderId, address);
    }

    @Override
    public boolean updateTableNumber(long orderId, int tableNumber) {
        return orderDao.updateTableNumber(orderId, tableNumber);
    }

    @Override
    public boolean delete(long orderId) {
        return orderDao.delete(orderId);
    }

}
