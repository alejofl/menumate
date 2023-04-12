package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.persistance.OrderDao;
import ar.edu.itba.paw.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;

    @Autowired
    public OrderServiceImpl(final OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order create(long orderTypeId, long restaurantId, long userId) {
        return orderDao.create(orderTypeId, restaurantId, userId);
    }

    @Override
    public Optional<Order> findOrderById(long orderId) {
        return orderDao.findOrderById(orderId);
    }

    @Override
    public List<Order> findOrdersByUserId(long userId, long restaurantId) {
        return orderDao.findOrdersByUserId(userId, restaurantId);
    }

    @Override
    public List<Order> findOrdersByRestaurantId(long restaurantId) {
        return orderDao.findOrdersByRestaurantId(restaurantId);
    }

    @Override
    public List<Order> findByOrdersBetweenDates(LocalDateTime start, LocalDateTime end, long restaurantId) {
        return orderDao.findByOrdersBetweenDates(start, end, restaurantId);
    }

    @Override
    public List<Order> findByOrdersAddress(String address, long restaurantId) {
        return orderDao.findByOrdersAddress(address, restaurantId);
    }

    @Override
    public List<Order> findByOrdersTableNumber(int tableNumber, long restaurantId) {
        return orderDao.findByOrdersTableNumber(tableNumber, restaurantId);
    }

    @Override
    public List<Order> findByOrdersTypeId(long orderTypeId, long restaurantId) {
        return orderDao.findByOrdersTypeId(orderTypeId, restaurantId);
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
