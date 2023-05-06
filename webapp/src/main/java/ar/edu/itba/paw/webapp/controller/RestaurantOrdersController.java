package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.model.util.PaginatedResult;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.webapp.form.PagingForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;

@Controller
public class RestaurantOrdersController {

    private static final int DEFAULT_ORDERS_PAGE_SIZE = 20;

    @Autowired
    private OrderService orderService;

    private ModelAndView restaurantOrders(int id, int page, int size, OrderStatus orderStatus) {

        PaginatedResult<Order> orders = orderService.getByRestaurant(id, page, size, orderStatus);

        ModelAndView mav = new ModelAndView("menu/restaurant_orders");

        mav.addObject("orders", orders.getResult());
        mav.addObject("orderCount", orders.getTotalCount());
        mav.addObject("pageCount", orders.getTotalPageCount());
        mav.addObject("id", id);

        return mav;
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/pending", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersPending(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("menu/restaurant_orders");
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
            return mav;
        }

        return restaurantOrders(id, paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE), OrderStatus.PENDING);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/confirmed", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersConfirmed(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("menu/restaurant_orders");
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
            return mav;
        }

        return restaurantOrders(id, paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE), OrderStatus.CONFIRMED);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/ready", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersReady(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {

        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("menu/restaurant_orders");
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
            return mav;
        }

        return restaurantOrders(id, paging.getPageOrDefault(), paging.getSizeOrDefault(DEFAULT_ORDERS_PAGE_SIZE), OrderStatus.READY);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders", method = RequestMethod.GET)
    public ModelAndView restaurantOrders(@PathVariable final int id) {
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/pending", id));
    }

    @RequestMapping(value = "/restaurants/{restaurantId:\\d+}/orders/{orderId:\\d+}/confirm", method = RequestMethod.GET)
    public ModelAndView confirmOrder(@PathVariable final int restaurantId, @PathVariable final int orderId) {
        orderService.markAsConfirmed(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/pending", restaurantId));
    }

    @RequestMapping(value = "/restaurants/{restaurantId:\\d+}/orders/{orderId:\\d+}/ready", method = RequestMethod.GET)
    public ModelAndView readyOrder(@PathVariable final int restaurantId, @PathVariable final int orderId) {
        orderService.markAsReady(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/confirmed", restaurantId));
    }

    @RequestMapping(value = "/restaurants/{restaurantId:\\d+}/orders/{orderId:\\d+}/deliver", method = RequestMethod.GET)
    public ModelAndView deliverOrder(@PathVariable final int restaurantId, @PathVariable final int orderId) {
        orderService.markAsDelivered(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/deliver", restaurantId));
    }
}
