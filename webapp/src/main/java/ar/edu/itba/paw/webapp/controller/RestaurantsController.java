package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.webapp.exception.IllegalOrderTypeException;
import ar.edu.itba.paw.webapp.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.webapp.form.CartItem;
import ar.edu.itba.paw.webapp.form.CheckoutForm;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RestaurantsController {
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/restaurants/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView restaurantMenu(@PathVariable final long id, @ModelAttribute("checkoutForm") final CheckoutForm form) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);

        final List<Pair<Category, List<Product>>> menu = restaurantService.getMenu(id);
        mav.addObject("menu", menu);

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}", method = RequestMethod.POST)
    public ModelAndView restaurantMenu(
            @PathVariable final long id,
            @Valid @ModelAttribute("checkoutForm") final CheckoutForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return restaurantMenu(id, form);
        }

        User user = userService.createIfNotExists(form.getEmail(), form.getName());

        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < form.getCart().size(); i++) {
            CartItem cartItem = form.getCart().get(i);
            items.add(orderService.createOrderItem(cartItem.getProductId(), i, cartItem.getQuantity(), cartItem.getComment()));
        }

        Order order;
        int orderType = form.getOrderType();
        if (orderType == OrderType.DINE_IN.ordinal()) {
            order = orderService.createDineIn(form.getRestaurantId(), form.getName(), form.getEmail(), form.getTableNumber(), items);
        } else if (orderType == OrderType.TAKEAWAY.ordinal()) {
            order = orderService.createTakeAway(form.getRestaurantId(), form.getName(), form.getEmail(), items);
        } else if (orderType == OrderType.DELIVERY.ordinal()) {
            order = orderService.createDelivery(form.getRestaurantId(), form.getName(), form.getEmail(), form.getAddress(), items);
        } else {
            throw new IllegalOrderTypeException("Order type not supported");
        }

        // FIXME: how do we handle this?
        try {
            emailService.sendUserOrderConfirmation(user, order);
            emailService.sendRestaurantOrderConfirmation(
                    restaurantService.getById(form.getRestaurantId()).orElseThrow(RestaurantNotFoundException::new),
                    order
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return new ModelAndView("redirect:/thankyou");
    }

    @RequestMapping("/thankyou")
    public ModelAndView thankYou(HttpServletRequest request) {
        return new ModelAndView("menu/thankyou");
    }
}
