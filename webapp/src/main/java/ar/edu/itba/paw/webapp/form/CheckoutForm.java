package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.OrderType;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

public class CheckoutForm {

    @NotNull
    @Size(max=64)
    private String name;
    @Email
    @NotNull
    private String email;
    @Min(0)
    private int tableNumber;
    private String address;
    @NotNull
    private int orderType;
    @NotNull
    private int restaurantId;
    @NotNull
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

    public int getOrderType() {
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

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setCart(List<CartItem> cart) {
        this.cart = cart;
    }
}
