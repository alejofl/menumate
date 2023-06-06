package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.InvalidOrderTypeException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.form.CartItem;
import ar.edu.itba.paw.webapp.form.CheckoutForm;
import ar.edu.itba.paw.webapp.form.CreateRestaurantForm;
import ar.edu.itba.paw.webapp.form.PagingForm;
import ar.edu.itba.paw.webapp.form.validation.PreProcessingCheckoutFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class RestaurantsController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantRoleService restaurantRoleService;

    @Autowired
    private ReviewService reviewService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new PreProcessingCheckoutFormValidator(binder.getValidator()));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView restaurantMenu(
            @PathVariable final int id,
            @ModelAttribute("checkoutForm") final CheckoutForm form,
            final Boolean formError
    ) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);

        Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        Optional<RestaurantRoleLevel> level;
        boolean admin = false;
        boolean order_viewer = false;
        if (currentUserId != null && (level = restaurantRoleService.getRole(currentUserId, id)).isPresent()) {
            admin = level.get().hasPermissionOf(RestaurantRoleLevel.ADMIN);
            order_viewer = level.get().hasPermissionOf(RestaurantRoleLevel.ORDER_HANDLER);
        }
        mav.addObject("admin", admin);
        mav.addObject("order_viewer", order_viewer);

        final AverageCountPair average = reviewService.getRestaurantAverage(id);
        mav.addObject("average", Math.round(average.getAverage()));
        mav.addObject("ratingCount", average.getCount());
        final List<Review> reviews = reviewService.getByRestaurant(id, 1, 30).getResult();
        mav.addObject("reviews", reviews);
        mav.addObject("tags", restaurant.getTags());

        final List<Category> menu = categoryService.getByRestaurantSortedByOrder(id);
        mav.addObject("menu", menu);

        mav.addObject("formError", formError);
        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders", method = RequestMethod.POST)
    public ModelAndView restaurantMenu(
            @PathVariable final int id,
            @Valid @ModelAttribute("checkoutForm") final CheckoutForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return restaurantMenu(id, form, true);
        }

        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < form.getCart().size(); i++) {
            CartItem cartItem = form.getCart().get(i);
            items.add(orderService.createOrderItem(cartItem.getProductId(), i + 1, cartItem.getQuantity(), cartItem.getComment()));
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
            throw new InvalidOrderTypeException("Order type not supported");
        }

        return thankYou(order.getOrderId(), order.getUser().getEmail());
    }

    private ModelAndView thankYou(
            final long orderId,
            final String email
    ) {
        ModelAndView mav = new ModelAndView("menu/thankyou");
        mav.addObject("orderId", orderId);
        mav.addObject("userExists", userService.isUserEmailRegisteredAndConsolidated(email));
        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/reviews", method = RequestMethod.GET)
    public ModelAndView restaurantReviews(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_reviews");
        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }
        PaginatedResult<Review> reviews = reviewService.getByRestaurant(id, paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_RESTAURANT_PAGE_SIZE));
        mav.addObject("reviews", reviews.getResult());
        return mav;
    }
}
