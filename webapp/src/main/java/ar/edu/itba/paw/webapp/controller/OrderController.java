package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.OrderNotFoundException;
import ar.edu.itba.paw.model.Order;
import ar.edu.itba.paw.model.OrderItem;
import ar.edu.itba.paw.service.OrderService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.OrderDto;
import ar.edu.itba.paw.webapp.dto.OrderItemDto;
import ar.edu.itba.paw.webapp.form.CheckoutForm;
import ar.edu.itba.paw.webapp.form.GetOrdersForm;
import ar.edu.itba.paw.webapp.form.UpdateOrderStatusForm;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path(UriUtils.ORDERS_URL)
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
    @Produces(CustomMediaType.APPLICATION_ORDERS)
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

        final List<OrderDto> dtoList = OrderDto.fromOrderCollection(uriInfo, orderPage.getResult());
        Response.ResponseBuilder builder = Response.ok(new GenericEntity<List<OrderDto>>(dtoList) {});
        return ControllerUtils.addPagingLinks(builder, orderPage, uriInfo).build();
    }

    @GET
    @Path("/{orderId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_ORDERS)
    public Response getOrderById(@PathParam("orderId") final long orderId, @Context Request request) {
        final Order order = orderService.getById(orderId).orElseThrow(OrderNotFoundException::new);
        return ControllerUtils.buildResponseUsingLastModified(request, order.getOrderLastUpdate(), () -> OrderDto.fromOrder(uriInfo, order));
    }

    @PATCH
    @Path("/{orderId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_ORDERS)
    public Response updateOrderStatus(
            @PathParam("orderId") final long orderId,
            @Valid @NotNull final UpdateOrderStatusForm updateOrderStatusForm
    ) {
        orderService.advanceOrderStatus(orderId, updateOrderStatusForm.getStatusAsEnum());
        return Response.noContent().build();
    }

    @GET
    @Path("/{orderId:\\d+}/items")
    @Produces(CustomMediaType.APPLICATION_ORDER_ITEMS)
    public Response getOrderItemsById(@PathParam("orderId") final long orderId) {
        final List<OrderItem> items = orderService.getOrderItemsById(orderId).orElseThrow(OrderNotFoundException::new);
        final List<OrderItemDto> dtoList = OrderItemDto.fromOrderItemCollection(uriInfo, items);
        return Response.ok(new GenericEntity<List<OrderItemDto>>(dtoList) {}).build();
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_ORDERS)
    public Response createOrder(@Valid @NotNull final CheckoutForm checkoutForm) {
        final Order order = orderService.create(
                checkoutForm.getOrderTypeAsEnum(),
                checkoutForm.getRestaurantId(),
                checkoutForm.getName(),
                checkoutForm.getEmail(),
                checkoutForm.getTableNumber(),
                checkoutForm.getAddress(),
                checkoutForm.getCartAsOrderItems(orderService)
        );

        return Response.created(UriUtils.getOrderUri(uriInfo, order.getOrderId())).build();
    }
}
