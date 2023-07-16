package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.RestaurantNotFoundException;
import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantDetails;
import ar.edu.itba.paw.service.RestaurantService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.dto.RestaurantDetailsDto;
import ar.edu.itba.paw.webapp.dto.RestaurantDto;
import ar.edu.itba.paw.webapp.form.FilterForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    public Response getRestaurants(@BeanParam FilterForm filterForm) {
        PaginatedResult<RestaurantDetails> restaurantDetails = restaurantService.search(
                filterForm.getSearch(),
                filterForm.getPageOrDefault(),
                filterForm.getSizeOrDefault(ControllerUtils.DEFAULT_SEARCH_PAGE_SIZE),
                filterForm.getOrderBy(),
                filterForm.getDescendingOrDefault(),
                filterForm.getTags(),
                filterForm.getSpecialties()
        );
        List<RestaurantDetailsDto> restaurantDetailsDtos = RestaurantDetailsDto.fromRestaurantDetailsList(restaurantDetails.getResult());

        Response.ResponseBuilder responseBuilder = Response.ok(new GenericEntity<List<RestaurantDetailsDto>>(restaurantDetailsDtos){});
        ControllerUtils.addPagingLinks(responseBuilder, restaurantDetails, uriInfo);
        return responseBuilder.build();
    }

    @GET
    @Path("/{restaurantId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRestaurantById(@PathParam("restaurantId") final long restaurantId) {
        final Restaurant restaurant = restaurantService.getById(restaurantId).orElseThrow(RestaurantNotFoundException::new);
        return Response.ok(RestaurantDto.fromRestaurant(restaurant)).build();
    }
}
