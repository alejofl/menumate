package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {
    private String email;
    private String name;
    private LocalDateTime dateJoined;
    private boolean isActive;
    private String preferredLanguage;
    private String role;

    private URI selfUrl;
    private URI addressesUrl;
    private URI ordersUrl;
    private URI reviewsUrl;
    private URI restaurantsEmployedAtUrl;

    public static UserDto fromUser(final UriInfo uriInfo, final User user) {
        final UserDto dto = new UserDto();
        dto.email = user.getEmail();
        dto.name = user.getName();
        dto.dateJoined = user.getDateJoined();
        dto.isActive = user.getIsActive();
        dto.preferredLanguage = user.getPreferredLanguage();

        dto.selfUrl = UriUtils.getUserUri(uriInfo, user.getUserId());
        dto.addressesUrl = UriUtils.getUserAddressesUri(uriInfo, user.getUserId());
        dto.ordersUrl = UriUtils.getOrdersByUserUri(uriInfo, user.getUserId());
        dto.reviewsUrl = UriUtils.getReviewsByUserUri(uriInfo, user.getUserId());
        dto.restaurantsEmployedAtUrl = UriUtils.getUserEmployedAtUri(uriInfo, user.getUserId());

        if(user.hasRole()) {
            dto.role = user.getRole().getLevel().getMessageCode().replaceAll("^ROLE_", "").toLowerCase();
        }

        return dto;
    }

    public static List<UserDto> fromUserCollection(final UriInfo uriInfo, final Collection<User> users) {
        return users.stream().map(u -> fromUser(uriInfo, u)).collect(Collectors.toList());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(LocalDateTime dateJoined) {
        this.dateJoined = dateJoined;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean active) {
        isActive = active;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getAddressesUrl() {
        return addressesUrl;
    }

    public void setAddressesUrl(URI addressesUrl) {
        this.addressesUrl = addressesUrl;
    }

    public URI getOrdersUrl() {
        return ordersUrl;
    }

    public void setOrdersUrl(URI ordersUrl) {
        this.ordersUrl = ordersUrl;
    }

    public URI getReviewsUrl() {
        return reviewsUrl;
    }

    public void setReviewsUrl(URI reviewsUrl) {
        this.reviewsUrl = reviewsUrl;
    }

    public URI getRestaurantsEmployedAtUrl() {
        return restaurantsEmployedAtUrl;
    }

    public void setRestaurantsEmployedAtUrl(URI restaurantsEmployedAtUrl) {
        this.restaurantsEmployedAtUrl = restaurantsEmployedAtUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
