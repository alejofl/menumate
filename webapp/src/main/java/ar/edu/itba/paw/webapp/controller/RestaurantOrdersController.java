package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.exception.OrderNotFoundException;
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
    @Autowired
    private OrderService orderService;

    private ModelAndView restaurantOrders(
            final PagingForm paging,
            final BindingResult errors,
            final int id,
            final OrderStatus orderStatus
    ) {
        if (errors.hasErrors()) {
            ModelAndView mav = new ModelAndView("menu/restaurant_orders");
            mav.addObject("error", Boolean.TRUE);
            paging.clear();
            return mav;
        }

        PaginatedResult<Order> orders = orderService.getByRestaurant(id, paging.getPageOrDefault(), paging.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE), orderStatus);

        ModelAndView mav = new ModelAndView("menu/restaurant_orders");

        mav.addObject("orders", orders.getResult());
        mav.addObject("status", orderStatus.getMessageCode());
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
        return restaurantOrders(paging, errors, id, OrderStatus.PENDING);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/confirmed", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersConfirmed(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {
        return restaurantOrders(paging, errors, id, OrderStatus.CONFIRMED);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/ready", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersReady(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {
        return restaurantOrders(paging, errors, id, OrderStatus.READY);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/delivered", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersDelivered(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {
        return restaurantOrders(paging, errors, id, OrderStatus.DELIVERED);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders/cancelled", method = RequestMethod.GET)
    public ModelAndView restaurantOrdersCancelled(
            @Valid final PagingForm paging,
            final BindingResult errors,
            @PathVariable final int id
    ) {
        return restaurantOrders(paging, errors, id, OrderStatus.CANCELLED);
    }

    @RequestMapping(value = "/restaurants/{id:\\d+}/orders", method = RequestMethod.GET)
    public ModelAndView restaurantOrders(@PathVariable final int id) {
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/pending", id));
    }

    @RequestMapping(value = "/orders/{orderId:\\d+}/confirm", method = RequestMethod.POST)
    public ModelAndView confirmOrder(@PathVariable final int orderId) {
        Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        boolean success = orderService.markAsConfirmed(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/pending%s", order.getRestaurant().getRestaurantId(), success ? "" : "?error=1"));
    }

    @RequestMapping(value = "/orders/{orderId:\\d+}/ready", method = RequestMethod.POST)
    public ModelAndView readyOrder(@PathVariable final int orderId) {
        Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        boolean success = orderService.markAsReady(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/confirmed%s", order.getRestaurant().getRestaurantId(), success ? "" : "?error=1"));
    }

    @RequestMapping(value = "/orders/{orderId:\\d+}/deliver", method = RequestMethod.POST)
    public ModelAndView deliverOrder(@PathVariable final int orderId) {
        Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        boolean success = orderService.markAsDelivered(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/ready%s", order.getRestaurant().getRestaurantId(), success ? "" : "?error=1"));
    }

    @RequestMapping(value = "/orders/{orderId:\\d+}/cancel", method = RequestMethod.POST)
    public ModelAndView cancelOrder(@PathVariable final int orderId) {
        Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        boolean success = orderService.markAsCancelled(orderId);
        return new ModelAndView(String.format("redirect:/restaurants/%d/orders/pending%s", order.getRestaurant().getRestaurantId(), success ? "" : "?error=1"));
    }
}
