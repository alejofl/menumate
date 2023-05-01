package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.Product;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.ProductService;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    private int getUserId(String name, String email){
        return userService.createIfNotExists(email, name).getUserId();
    }

    @Override
    public Order createDelivery(int restaurantId, int userId, String address, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, userId, address, items);
    }

    @Override
    public Order createDelivery(int restaurantId, String name, String email, String address, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, getUserId(name, email), address, items);
    }

    @Override
    public Order createDineIn(int restaurantId, int userId, int tableNumber, List<OrderItem> items) {
        return orderDao.create(OrderType.DINE_IN, restaurantId, userId, tableNumber, items);
    }

    @Override
    public Order createDineIn(int restaurantId, String name, String email, int tableNumber, List<OrderItem> items) {
        return orderDao.create(OrderType.DINE_IN, restaurantId, getUserId(name, email), tableNumber, items);
    }

    @Override
    public Order createTakeAway(int restaurantId, int userId, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, userId, items);
    }

    @Override
    public Order createTakeAway(int restaurantId, String name, String email, List<OrderItem> items) {
        return orderDao.create(OrderType.DELIVERY, restaurantId, getUserId(name, email), items);
    }


    @Override
    public OrderItem createOrderItem(int productId, int lineNumber, int quantity, String comment) {
        Product product = productService.getById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product id"));
        return orderDao.createOrderItem(product, lineNumber, quantity, comment);
    }

    @Override
    public Optional<Order> getById(int orderId) {
        return orderDao.getById(orderId);
    }

    @Override
    public PaginatedResult<Order> getByUser(int userId, int pageNumber, int pageSize) {
        return orderDao.getByUser(userId, pageNumber, pageSize);
    }

    @Override
    public PaginatedResult<Order> getByRestaurant(int restaurantId, int pageNumber, int pageSize) {
        return orderDao.getByRestaurant(restaurantId, pageNumber, pageSize);
    }

    /*@Override
    public List<Order> getOrderedBetweenDates(int restaurantId, LocalDateTime start, LocalDateTime end) {
        return orderDao.getByRestaurantOrderedBetweenDates(restaurantId, start, end);
    }

    @Override
    public List<Order> getByAddress(int restaurantId, String address) {
        return orderDao.getByRestaurantAndAddress(restaurantId, address);
    }

    @Override
    public List<Order> getByTableNumber(int restaurantId, int tableNumber) {
        return orderDao.getByRestaurantAndTableNumber(restaurantId, tableNumber);
    }

    @Override
    public List<Order> getByOrderTypeAndRestaurant(OrderType orderType, int restaurantId) {
        return orderDao.getByOrderTypeAndRestaurant(orderType, restaurantId);
    }*/

    @Override
    public boolean updateAddress(int orderId, String address) {
        return orderDao.updateAddress(orderId, address);
    }

    @Override
    public boolean updateTableNumber(int orderId, int tableNumber) {
        return orderDao.updateTableNumber(orderId, tableNumber);
    }

    @Override
    public boolean delete(int orderId) {
        return orderDao.delete(orderId);
    }

}
