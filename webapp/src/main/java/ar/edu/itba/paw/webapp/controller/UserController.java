package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.UserAddressDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.PatchAddressesForm;
import ar.edu.itba.paw.webapp.form.RegisterForm;
import ar.edu.itba.paw.webapp.form.UpdateUserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("users")
@Component
public class UserController {

    private final UserService userService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(final UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/{userId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("userId") final long userId) {
        User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDto.fromUser(user)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid final RegisterForm registerForm) {
        final User user = userService.createOrConsolidate(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        return Response.created(uriInfo.getBaseUriBuilder().path("/users").path(String.valueOf(user.getUserId())).build()).build();
    }

    @PUT
    @Path("/{userId:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("userId") final long userId,
            @Valid final UpdateUserForm updateUserForm
    ) {
        userService.updateUser(userId, updateUserForm.getName(), updateUserForm.getPreferredLanguage());
        return Response.noContent().build();
    }

    @GET
    @Path("/{userId:\\d+}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddresses(@PathParam("userId") final long userId) {
        User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        List<UserAddressDto> dtoList = UserAddressDto.fromUserAddressCollection(user.getAddresses());
        return Response.ok(new GenericEntity<List<UserAddressDto>>(dtoList){}).build();
    }

    @PATCH
    @Path("/{userId:\\d+}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserAddress(
            @PathParam("userId") final long userId,
            @Valid final PatchAddressesForm patchAddressesForm
    ) {
        if (patchAddressesForm.getAction().equals("remove")) {
            userService.deleteAddress(userId, patchAddressesForm.getAddress());
        } else {
            userService.registerAddress(userId, patchAddressesForm.getAddress(), patchAddressesForm.getName());
        }

        return Response.noContent().build();
    }
}
