package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.UserAddressNotFoundException;
import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;
import ar.edu.itba.paw.service.UserRoleService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import ar.edu.itba.paw.webapp.dto.UserAddressDto;
import ar.edu.itba.paw.webapp.dto.UserDto;
import ar.edu.itba.paw.webapp.form.*;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final UserRoleService userRoleService;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public UserController(final UserService userService, final JwtTokenUtil jwtTokenUtil, final UserRoleService userRoleService) {
        this.userService = userService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userRoleService = userRoleService;
    }

    @GET
    @Path("/{userId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_USER)
    public Response getUser(@PathParam("userId") final long userId) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        return Response.ok(UserDto.fromUser(uriInfo, user)).build();
    }

    @POST
    @Consumes({CustomMediaType.APPLICATION_USER})
    public Response registerUser(
            @Valid @NotNull final RegisterForm registerForm,
            @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) final String language
    ) {
        final User user = userService.createOrConsolidate(registerForm.getEmail(), registerForm.getPassword(), registerForm.getName(), language);
        return Response.created(UriUtils.getUserUri(uriInfo, user.getUserId())).build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_USER)
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
    @Produces(CustomMediaType.APPLICATION_USER_ADDRESS)
    public Response getUserAddresses(@PathParam("userId") final long userId) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        final List<UserAddressDto> dtoList = UserAddressDto.fromUserAddressCollection(uriInfo, user.getAddresses());
        return Response.ok(new GenericEntity<List<UserAddressDto>>(dtoList) {}).build();
    }

    @GET
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_USER_ADDRESS)
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
    @Produces(CustomMediaType.APPLICATION_USER_ADDRESS)
    public Response registerUserAddress(
            @PathParam("userId") final long userId,
            @Valid @NotNull final AddUserAddressForm addUserAddressForm
    ) {
        final UserAddress address = userService.registerAddress(userId, addUserAddressForm.getAddressTrimmedOrNull(), addUserAddressForm.getName());
        return Response.created(UriUtils.getUserAddressUri(uriInfo, address)).build();
    }

    @PATCH
    @Path("/{userId:\\d+}/addresses/{addressId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_USER_ADDRESS)
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
    public Response deleteUserAddress(
            @PathParam("userId") final long userId,
            @PathParam("addressId") final long addressId
    ) {
        userService.deleteAddress(userId, addressId);
        return Response.noContent().build();
    }

    @GET
    @Produces(CustomMediaType.APPLICATION_USER_ROLE)
    @PreAuthorize("#getUserRoleLevelForm.role != null and hasRole('MODERATOR')")
    public Response getUsersWithRoleLevel(@Valid @BeanParam final GetUserRoleLevelForm getUserRoleLevelForm) {
        final List<User> users = userRoleService.getByRole(getUserRoleLevelForm.getRoleAsEnum());
        final List<UserDto> userDtos = UserDto.fromUserCollection(uriInfo, users);
        return Response.ok((new GenericEntity<List<UserDto>>(userDtos) {})).build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_USER_ROLE)
    public Response updateUserRoleLevel(
            @PathParam("userId") final long userId,
            @Valid @NotNull final PatchUserRoleLevelForm patchUserRoleLevelForm
    ) {
        final User user = userService.getById(userId).orElseThrow(UserNotFoundException::new);
        userRoleService.setRole(user.getEmail(), patchUserRoleLevelForm.getRoleAsEnum(), user.getPreferredLanguage());
        return Response.noContent().build();
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_USER_ROLE)
    public Response createUserRole(
            @Valid @NotNull final PostUserRoleLevelForm addUserRoleForm,
            @HeaderParam(HttpHeaders.ACCEPT_LANGUAGE) final String language
    ) {
        userRoleService.setRole(addUserRoleForm.getEmail(), addUserRoleForm.getRoleAsEnum(), language);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{userId:\\d+}")
    public Response deleteUserRole(@PathParam("userId") final long userId) {
        userRoleService.deleteRole(userId);
        return Response.noContent().build();
    }

    @POST
    @Consumes(CustomMediaType.APPLICATION_USER_PASSWORD)
    public Response createPasswordResetToken(@Valid @NotNull final ResetPasswordForm resetPasswordForm) {
        userService.sendPasswordResetToken(resetPasswordForm.getEmail());
        return Response.noContent().build();
    }

    @PATCH
    @Path("/{userId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_USER_PASSWORD)
    public Response editUserPasswordWithToken(
            @PathParam("userId") long userId,
            @Valid @NotNull NewPasswordForm newPasswordForm
    ) {
        userService.updatePassword(userId, newPasswordForm.getPassword());
        return Response.noContent().build();
    }
}
