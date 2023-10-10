package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserAddressNotFoundException;
import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.UserAddressDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path(UriUtils.USERS_URL)
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
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registerUser(@Valid @NotNull final RegisterForm registerForm) {
        final User user = userService.createOrConsolidate(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName());
        return Response.created(UriUtils.getUserUri(uriInfo, user.getUserId())).build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(
            @PathParam("userId") final long userId,
            @Valid @NotNull final UpdateUserForm updateUserForm
    ) {
        userService.updateUser(
                userId,
                updateUserForm.getNameTrimmedOrNull(),
                updateUserForm.getPreferredLanguage()
        );
        return Response.noContent().build();
    }

    @GET
    @Path("/{userId:\\d+}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddresses(@PathParam("userId") final long userId) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        final List<UserAddressDto> dtoList = UserAddressDto.fromUserAddressCollection(uriInfo, user.getAddresses());
        return Response.ok(new GenericEntity<List<UserAddressDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId
    ) {
        final UserAddress address = userService.getAddressById(userId, addressId).orElseThrow(UserAddressNotFoundException::new);
        final UserAddressDto dto = UserAddressDto.fromUserAddress(uriInfo, address);
        return Response.ok(dto).build();
    }

    @POST
    @Path("/{userId:\\d+}/addresses")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUserAddress(
            @PathParam("userId") final long userId,
            @Valid @NotNull final AddUserAddressForm addUserAddressForm
    ) {
        final UserAddress address = userService.registerAddress(userId, addUserAddressForm.getAddressTrimmedOrNull(), addUserAddressForm.getName());
        return Response.created(UriUtils.getUserAddressUri(uriInfo, address)).build();
    }

    @PATCH
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId,
            @Valid @NotNull final UpdateUserAddressForm updateUserAddressForm

    ) {
        userService.updateAddress(userId, addressId, updateUserAddressForm.getAddress(), updateUserAddressForm.getName());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId
    ) {
        userService.deleteAddress(userId, addressId);
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
        final User user = userService.verifyUserAndDeleteVerificationToken(token).orElseThrow(UserNotFoundException::new);

        final String userUrl = UriUtils.getUserUri(uriInfo, user.getUserId()).toString();
        return Response.noContent()
                .header("MenuMate-AuthToken", jwtTokenUtil.generateAccessToken(user))
                .header("MenuMate-UserUrl", userUrl)
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
        final boolean success = userService.updatePasswordAndDeleteResetPasswordToken(token, resetPasswordForm.getPassword());
        if (!success)
            return Response.status(Response.Status.NOT_FOUND).build();

        return Response.noContent().build();
    }
}
