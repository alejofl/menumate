package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.OrderDto;
import ar.edu.itba.paw.webapp.form.GetOrdersForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("orders")
@Component
public class OrderController {

    private final OrderService orderService;
    private final AccessValidator accessValidator;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public OrderController(final OrderService orderService, final AccessValidator accessValidator) {
        this.orderService = orderService;
        this.accessValidator = accessValidator;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @PreAuthorize("@accessValidator.checkCanListOrders(#getOrdersForm.userId, #getOrdersForm.restaurantId)")
    public Response getOrders(@Valid @BeanParam final GetOrdersForm getOrdersForm) {
        PaginatedResult<Order> orderPage = orderService.get(
                getOrdersForm.getUserId(),
                getOrdersForm.getRestaurantId(),
                getOrdersForm.getStatusAsEnum(),
                getOrdersForm.getInProgress(),
                getOrdersForm.getDescending(),
                getOrdersForm.getPageOrDefault(),
                getOrdersForm.getSizeOrDefault(ControllerUtils.DEFAULT_ORDERS_PAGE_SIZE)
        );

        List<OrderDto> dtoList = OrderDto.fromOrderCollection(orderPage.getResult());
        Response.ResponseBuilder builder = Response.ok(new GenericEntity<List<OrderDto>>(dtoList){});
        return ControllerUtils.addPagingLinks(builder, orderPage, uriInfo).build();
    }

    @GET
    @Path("/{orderId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrderById(@PathParam("orderId") final long orderId) {
        Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        return Response.ok(OrderDto.fromOrder(order)).build();
    }
}
