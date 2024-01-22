package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.RoleNotFoundException;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.RestaurantRoleService;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.RestaurantRoleDto;
import ar.edu.itba.paw.webapp.form.AddRestaurantEmployeeForm;
import ar.edu.itba.paw.webapp.form.UpdateRestaurantEmployeeForm;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Objects;

@Path(UriUtils.RESTAURANTS_URL + "/{restaurantId:\\d+}/employees")
@Component
public class RestaurantEmployeeController {
    private final RestaurantRoleService restaurantRoleService;

    private final AccessValidator accessValidator;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RestaurantEmployeeController(final RestaurantRoleService restaurantRoleService, final AccessValidator accessValidator) {
        this.restaurantRoleService = restaurantRoleService;
        this.accessValidator = accessValidator;
    }

    @GET
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_EMPLOYEE)
    public Response getRestaurantEmployees(@PathParam("restaurantId") final long restaurantId) {
        final List<Pair<User, RestaurantRoleLevel>> roles = restaurantRoleService.getByRestaurant(restaurantId);
        final List<RestaurantRoleDto> dtoList = RestaurantRoleDto.fromCollection(uriInfo, restaurantId, roles);
        return Response.ok(new GenericEntity<List<RestaurantRoleDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{userId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_RESTAURANT_EMPLOYEE)
    public Response getRestaurantEmployeeByUserId(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("userId") final long userId,
            @Context Request request
    ) {
        final RestaurantRoleLevel role = restaurantRoleService.getRole(userId, restaurantId).orElseThrow(RoleNotFoundException::new);
        return ControllerUtils.buildResponseUsingEtag(request, Objects.hash(restaurantId, userId, role), ()-> RestaurantRoleDto.from(uriInfo, restaurantId, userId, role));
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_EMPLOYEE)
    public Response addRestaurantEmployee(
            @PathParam("restaurantId") final long restaurantId,
            @Valid @NotNull final AddRestaurantEmployeeForm addRestaurantEmployeeForm
    ) {
        final Pair<User, Boolean> userPair = restaurantRoleService.setRole(addRestaurantEmployeeForm.getEmail(), restaurantId, addRestaurantEmployeeForm.getRoleAsEnum());
        final User user = userPair.getKey();
        return Response.created(UriUtils.getRestaurantEmployeeUri(uriInfo, restaurantId, user.getUserId())).build();
    }

    @PUT
    @Path("/{userId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_RESTAURANT_EMPLOYEE)
    public Response updateRestaurantEmployeeByUserId(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("userId") final long userId,
            @Valid @NotNull final UpdateRestaurantEmployeeForm updateRestaurantEmployeeForm
    ) {
        restaurantRoleService.updateRole(userId, restaurantId, updateRestaurantEmployeeForm.getRoleAsEnum());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{userId:\\d+}")
    public Response deleteRestaurantEmployeeByUserId(
            @PathParam("restaurantId") final long restaurantId,
            @PathParam("userId") final long userId
    ) {
        restaurantRoleService.deleteRole(userId, restaurantId);
        return Response.noContent().build();
    }
}
