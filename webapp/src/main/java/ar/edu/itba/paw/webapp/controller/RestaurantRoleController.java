package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.RestaurantRoleService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.RestaurantRoleDetailsDto;
import ar.edu.itba.paw.webapp.dto.RestaurantRoleDto;
import ar.edu.itba.paw.webapp.form.GetRestaurantRolesForm;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;

@Path("restaurantRoles")
@Component
public class RestaurantRoleController {
    private final RestaurantRoleService restaurantRoleService;

    private final AccessValidator accessValidator;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public RestaurantRoleController(final RestaurantRoleService restaurantRoleService, final AccessValidator accessValidator) {
        this.restaurantRoleService = restaurantRoleService;
        this.accessValidator = accessValidator;
    }

    @GET
    @PreAuthorize("hasRole('MODERATOR') or @accessValidator.checkCanListRestaurantRoles(#getRestaurantRolesForm.userId, #getRestaurantRolesForm.restaurantId)")
    public Response getRestaurantRoles(@Valid @BeanParam final GetRestaurantRolesForm getRestaurantRolesForm) {
        // Either restaurantId is not null or userId is not null.

        if (getRestaurantRolesForm.getRestaurantId() != null) {
            final List<Pair<User, RestaurantRoleLevel>> roles = restaurantRoleService.getByRestaurant(getRestaurantRolesForm.getRestaurantId());
            final List<RestaurantRoleDto> dtoList = RestaurantRoleDto.fromCollection(uriInfo, getRestaurantRolesForm.getRestaurantId(), roles);
            return Response.ok(new GenericEntity<List<RestaurantRoleDto>>(dtoList){}).build();
        }

        if (getRestaurantRolesForm.getUserId() != null) {
            final PaginatedResult<RestaurantRoleDetails> roles = restaurantRoleService.getByUser(getRestaurantRolesForm.getUserId(), getRestaurantRolesForm.getPageOrDefault(), getRestaurantRolesForm.getSizeOrDefault(ControllerUtils.DEFAULT_MYRESTAURANTS_PAGE_SIZE));
            final List<RestaurantRoleDetailsDto> dtoList = RestaurantRoleDetailsDto.fromCollection(uriInfo, roles.getResult());
            Response.ResponseBuilder response = Response.ok(new GenericEntity<List<RestaurantRoleDetailsDto>>(dtoList){});
            return ControllerUtils.addPagingLinks(response, roles, uriInfo).build();
        }

        return Response.status(418, "I'm a Teapot").build(); // This should never happen
    }
}
