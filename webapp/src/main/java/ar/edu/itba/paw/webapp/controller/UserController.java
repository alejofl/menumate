package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderType;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.form.CreateRestaurantForm;
import ar.edu.itba.paw.webapp.exception.OrderNotFoundException;
import ar.edu.itba.paw.webapp.form.PagingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;

@Controller
public class UserController {
    private static final int DEFAULT_ORDERS_PAGE_SIZE = 20;

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private RestaurantService restaurantService;

    @RequestMapping(value = "/orders", method = RequestMethod.GET)
    public ModelAndView myOrders(
            @Valid final PagingForm paging,
            final BindingResult errors
    ) {
        ModelAndView mav = new ModelAndView("user/myorders");

        if (errors.hasErrors()) {
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
        }

        User currentUser = ControllerUtils.getCurrentUserOrThrow(userService);
        PaginatedResult<Order> orders = orderService.getByUser(currentUser.getUserId(), paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE));
        mav.addObject("orders", orders.getResult());
        mav.addObject("orderCount", orders.getTotalCount());
        mav.addObject("pageCount", orders.getTotalPageCount());
        return mav;
    }

    @RequestMapping(value = "/orders/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView order(@PathVariable int id) {
        ModelAndView mav = new ModelAndView("user/order");
        mav.addObject("order", orderService.getById(id).orElseThrow(OrderNotFoundException::new));
        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders", method = RequestMethod.GET)
    public ModelAndView restaurantOrder(@Valid final PagingForm paging, @PathVariable int id) {

        PaginatedResult<Order> orders = orderService.getByRestaurant(id, paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE));
        ModelAndView mav = new ModelAndView("menu/restaurant_orders");
        mav.addObject("orders", orders.getResult());
        mav.addObject("orderCount", orders.getTotalCount());
        mav.addObject("pageCount", orders.getTotalPageCount());
        mav.addObject ("id", id);
        return mav;
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.GET)
    public ModelAndView createRestaurant(
            @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form
    ) {
        return new ModelAndView("user/create_restaurant");
    }

    @RequestMapping(value = "/restaurants/create", method = RequestMethod.POST)
    public ModelAndView createRestaurantForm(
            @Valid @ModelAttribute("createRestaurantForm") final CreateRestaurantForm form,
            final BindingResult errors
    ) throws IOException {
        // TODO recover images when errors occur on other field
        if (errors.hasErrors()) {
            return createRestaurant(form);
        }

        int restaurantId = restaurantService.create(
                form.getName(),
                form.getDescription(),
                form.getAddress(),
                ControllerUtils.getCurrentUserOrThrow(userService),
                form.getLogo().getBytes(),
                form.getPortrait1().getBytes(),
                form.getPortrait2().getBytes()
        );

        return new ModelAndView(String.format("redirect:/restaurants/%d", restaurantId));
    }
}
