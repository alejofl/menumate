package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.webapp.form.validation.AllProductsFromSameRestaurant;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import ar.edu.itba.paw.webapp.form.validation.ValidFieldsByOrderType;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@ValidFieldsByOrderType(orderTypeField = "orderType", tableNumberField = "tableNumber", addressField = "address")
@AllProductsFromSameRestaurant(restaurantIdField = "restaurantId", cartItemsField = "cart")
public class CheckoutForm {

    @NotBlank(message = "{NotBlank.CheckoutForm.name}")
    @Size(max = 48, message = "{Size.CheckoutForm.name}")
    private String name;

    @NotBlank(message = "{NotBlank.CheckoutForm.email}")
    @Email(message = "{Email.CheckoutForm.email}")
    private String email;

    @Min(value = 1, message ="{Min.CheckoutForm.tableNumber}" )
    private Integer tableNumber;

    @Size(min = 3, max = 200, message = "{Size.CheckoutForm.address}")
    private String address;

    @NotBlank(message = "{NotBlank.CheckoutForm.orderType}")
    @EnumMessageCode(enumClass = OrderType.class, message = "{EnumMessageCode.CheckoutForm.orderType}")
    private String orderType;

    @NotNull(message = "{NotNull.CheckoutForm.restaurantId}")
    private Long restaurantId;

    @NotEmpty(message = "{NotEmpty.CheckoutForm.cart}")
    @Size(min = 1, max = 500, message = "{Size.CheckoutForm.cart}")
    @Valid
    private List<CartItem> cart;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(Integer tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderType() {
        return orderType;
    }

    public OrderType getOrderTypeAsEnum() {
        return OrderType.fromCode(orderType);
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<CartItem> getCart() {
        return cart;
    }

    public List<OrderItem> getCartAsOrderItems(final OrderService orderService) {
        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < cart.size(); i++) {
            CartItem cartItem = cart.get(i);
            items.add(orderService.createOrderItem(restaurantId, cartItem.getProductId(), i + 1, cartItem.getQuantity(), cartItem.getCommentTrimmedOrNull()));
        }

        return items;
    }

    public void setCart(List<CartItem> cart) {
        this.cart = cart;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
