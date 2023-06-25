package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.InvalidOrderTypeException;
import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.*;
import ar.edu.itba.paw.service.*;
import ar.edu.itba.paw.util.AverageCountPair;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.form.validation.PreProcessingCheckoutFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.swing.*;
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

    @Autowired
    private ReportService reportService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setValidator(new PreProcessingCheckoutFormValidator(binder.getValidator()));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView restaurantMenu(
            @PathVariable final int id,
            @ModelAttribute("checkoutForm") final CheckoutForm form,
            final Boolean formError,
            @ModelAttribute("reportForm") final ReportRestaurantForm reportRestaurantForm,
            final Boolean reportFormErrors
    ) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_menu");

        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);
        mav.addObject("restaurant", restaurant);

        Long currentUserId = ControllerUtils.getCurrentUserIdOrNull();
        Optional<RestaurantRoleLevel> level;
        boolean owner = false;
        boolean admin = false;
        boolean order_viewer = false;
        if (currentUserId != null && (level = restaurantRoleService.getRole(currentUserId, id)).isPresent()) {
            owner = level.get().hasPermissionOf(RestaurantRoleLevel.OWNER);
            admin = level.get().hasPermissionOf(RestaurantRoleLevel.ADMIN);
            order_viewer = level.get().hasPermissionOf(RestaurantRoleLevel.ORDER_HANDLER);
        }
        mav.addObject("owner", owner);
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
        final List<Promotion> promotions = restaurantService.getActivePromotions(id);
        mav.addObject("promotions", promotions);

        mav.addObject("dinein_wait_time", restaurantService.getAverageOrderCompletionTime(id, OrderType.DINE_IN, ControllerUtils.AVERAGE_WAIT_TIME_PERIOD).orElse(null));
        mav.addObject("takeaway_wait_time", restaurantService.getAverageOrderCompletionTime(id, OrderType.TAKEAWAY, ControllerUtils.AVERAGE_WAIT_TIME_PERIOD).orElse(null));
        mav.addObject("delivery_wait_time", restaurantService.getAverageOrderCompletionTime(id, OrderType.DELIVERY, ControllerUtils.AVERAGE_WAIT_TIME_PERIOD).orElse(null));

        /* NOTE:
         * This is a workaround to avoid IllegalStateException.
         * The problem is that when this method is called from another method (i.e. when there's an error on a form)
         * the ModelAttributes are not added to the model automatically, thus all the forms on the page but the faulty
         * one do not exist.
         * The solution to this involves using FlashAttributes and adding BindingResults manually for each form. This
         * solution also fixes the Reload Requested problem when reloading page after error on a form.
         * However, this method is not compatible with ModelAndView.
         */
        mav.addObject("checkoutForm", form);
        mav.addObject("reportForm", reportRestaurantForm);

        mav.addObject("formError", formError);
        mav.addObject("reportFormErrors", reportFormErrors);
        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders", method = RequestMethod.POST)
    public ModelAndView restaurantMenu(
            @PathVariable final int id,
            @Valid @ModelAttribute("checkoutForm") final CheckoutForm form,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return restaurantMenu(id, form, true, new ReportRestaurantForm(), false);
        }

        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < form.getCart().size(); i++) {
            CartItem cartItem = form.getCart().get(i);
            items.add(orderService.createOrderItem(id, cartItem.getProductId(), i + 1, cartItem.getQuantity(), cartItem.getComment()));
        }

        Order order = orderService.create(OrderType.fromOrdinal(form.getOrderType()), form.getRestaurantId(), form.getName(), form.getEmail(), form.getTableNumber(), form.getAddress(), items);

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
            @PathVariable final int id,
            @ModelAttribute("reviewReplyForm") final ReviewReplyForm reviewReplyForm,
            final Boolean reviewReplyFormErrors,
            @Valid final PagingForm paging,
            final BindingResult errors
    ) {
        final ModelAndView mav = new ModelAndView("menu/restaurant_reviews");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }

        PaginatedResult<Review> reviews = reviewService.getByRestaurant(id, paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_RESTAURANT_PAGE_SIZE));
        final Restaurant restaurant = restaurantService.getById(id).orElseThrow(RestaurantNotFoundException::new);

        mav.addObject("reviews", reviews.getResult());
        mav.addObject("reviewCount", reviews.getTotalCount());
        mav.addObject("pageCount", reviews.getTotalPageCount());

        mav.addObject("restaurant", restaurant);

        mav.addObject("reviewReplyFormErrors", reviewReplyFormErrors);
        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/reviews", method = RequestMethod.POST)
    public ModelAndView replyReview(
            @PathVariable final int id,
            @Valid @ModelAttribute("reviewReplyForm") final ReviewReplyForm reviewReplyForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            PagingForm pagingForm = new PagingForm();
            return restaurantReviews(
                    id,
                    reviewReplyForm,
                    true,
                    pagingForm,
                    new BeanPropertyBindingResult(pagingForm, "pagingForm")
            );
        }

        reviewService.replyToReview(reviewReplyForm.getOrderId(), reviewReplyForm.getReply());

        return new ModelAndView(String.format("redirect:/restaurants/%d/reviews", id));
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/delete", method = RequestMethod.POST)
    public ModelAndView deleteRestaurant(@PathVariable final int id) {
        restaurantService.delete(id);
        return new ModelAndView("redirect:/restaurants");
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/report", method = RequestMethod.POST)
    public ModelAndView reportRestaurant(
            @PathVariable final int id,
            @Valid @ModelAttribute("reportForm") final ReportRestaurantForm reportRestaurantForm,
            final BindingResult errors
    ) {
        if (errors.hasErrors()) {
            return restaurantMenu(id, new CheckoutForm(), false, reportRestaurantForm, true);
        }

        reportService.create(id, reportRestaurantForm.getUserId(), reportRestaurantForm.getComment());

        return new ModelAndView(String.format("redirect:/restaurants/%d", id));
    }
}
