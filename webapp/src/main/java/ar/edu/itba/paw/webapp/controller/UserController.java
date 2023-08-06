package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.UserAddressDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;
import java.util.Optional;

@Path("users")
@Component
public class UserController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(final UserService userService, final JwtTokenUtil jwtTokenUtil) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
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
    public Response registerUser(@Valid @NotNull final RegisterForm registerForm) {
        final User user = userService.createOrConsolidate(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        return Response.created(uriInfo.getBaseUriBuilder().path("/users").path(String.valueOf(user.getUserId())).build()).build();
    }

    @PUT
    @Path("/{userId:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("userId") final long userId,
            @Valid @NotNull final UpdateUserForm updateUserForm
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
            @Valid @NotNull final PatchAddressesForm patchAddressesForm
    ) {
        if (patchAddressesForm.getAction().equals("remove")) {
            userService.deleteAddress(userId, patchAddressesForm.getAddress());
        } else {
            userService.registerAddress(userId, patchAddressesForm.getAddress(), patchAddressesForm.getName());
        }

        return Response.noContent().build();
    }

    @POST
    @Path("verification-tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resendVerificationToken(@Valid @NotNull final EmailForm emailForm) {
        userService.resendUserVerificationToken(emailForm.getEmail());
        return Response.noContent().build();
    }

    @PUT
    @Path("verification-tokens/{token}")
    public Response verificationToken(@PathParam("token") final String token) {
        Optional<User> user = userService.verifyUserAndDeleteVerificationToken(token);
        if (!user.isPresent())
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.noContent()
                .header("Token", jwtTokenUtil.generateAccessToken(user.get()))
                .header("MenuMate-UserId", Long.toString(user.get().getUserId()))
                .build();
    }

    @POST
    @Path("resetpassword-tokens")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response sendResetPasswordToken(@Valid @NotNull final EmailForm emailForm) {
        userService.sendPasswordResetToken(emailForm.getEmail());
        return Response.noContent().build();
    }

    @PUT
    @Path("resetpassword-tokens/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetpasswordToken(
            @PathParam("token") final String token,
            @Valid @NotNull final ResetPasswordForm resetPasswordForm
    ) {
        boolean success = userService.updatePasswordAndDeleteResetPasswordToken(token, resetPasswordForm.getPassword());
        if (!success)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.noContent().build();
    }
}
