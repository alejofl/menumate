package ar.edu.itba.paw.webapp.form;

import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import java.util.List;

public class CheckoutForm {

    private String name;
    @Email
    private String email;
    @Min(1)
    private int tableNumber;
    private String address;
    private OrderType orderType;
    private int restaurantId;
    private List<CartItem> cart;

    public String getEmail() {
        return email;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getAddress() {
        return address;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public List<CartItem> getCart() {
        return cart;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setCart(List<CartItem> cart) {
        this.cart = cart;
    }
}
