package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantDetails;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.dto.RestaurantDetailsDto;
import ar.edu.itba.paw.webapp.dto.RestaurantDto;
import ar.edu.itba.paw.webapp.form.FilterForm;
import ar.edu.itba.paw.webapp.form.UpdateRestaurantForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("restaurants")
@Component
public class RestaurantController {
    private final RestaurantService restaurantService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RestaurantController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRestaurants(@Valid @BeanParam final FilterForm filterForm) {
        PaginatedResult<RestaurantDetails> pagedResult = restaurantService.search(
                filterForm.getSearch(),
                filterForm.getPageOrDefault(),
                filterForm.getSizeOrDefault(ControllerUtils.DEFAULT_SEARCH_PAGE_SIZE),
                filterForm.getOrderByAsEnum(),
                filterForm.getDescendingOrDefault(),
                filterForm.getTagsAsEnum(),
                filterForm.getSpecialtiesAsEnum()
        );

        List<RestaurantDetailsDto> dtoList = RestaurantDetailsDto.fromRestaurantDetailsCollection(pagedResult.getResult());
        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RestaurantDetailsDto>>(dtoList){});
        return ControllerUtils.addPagingLinks(responseBuilder, pagedResult, uriInfo).build();
    }

    @GET
    @Path("/{restaurantId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRestaurantById(@PathParam("restaurantId") final long restaurantId) {
        final Restaurant restaurant = restaurantService.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
        return Response.ok(RestaurantDto.fromRestaurant(restaurant)).build();
    }

    @PUT
    @Path("/{restaurantId:\\d+}")
    public Response updateRestaurantById(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final UpdateRestaurantForm updateRestaurantForm
    ) {
        restaurantService.update(
                restaurantId,
                updateRestaurantForm.getName(),
                updateRestaurantForm.getSpecialtyAsEnum(),
                updateRestaurantForm.getAddress(),
                updateRestaurantForm.getDescription(),
                updateRestaurantForm.getTagsAsEnum()
        );

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{restaurantId:\\d+}")
    public Response deleteRestaurantById(@PathParam("restaurantId") final long restaurantId) {
        restaurantService.delete(restaurantId);
        return Response.noContent().build();
    }
}
