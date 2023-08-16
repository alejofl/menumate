package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantRoleDto {
    private long userId;
    private long restaurantId;
    private String role;

    private URI userUrl;
    private URI restaurantUrl;

    public static RestaurantRoleDto from(final UriInfo uriInfo, final long restaurantId, final Pair<User, RestaurantRoleLevel> pair) {
        final RestaurantRoleDto dto = new RestaurantRoleDto();
        dto.userId = pair.getKey().getUserId();
        dto.restaurantId = restaurantId;
        dto.role = pair.getValue().getMessageCode();

        dto.userUrl = UriUtils.getUserUri(uriInfo, dto.userId);
        dto.restaurantUrl = UriUtils.getRestaurantUri(uriInfo, dto.restaurantId);

        return dto;
    }

    public static List<RestaurantRoleDto> fromCollection(final UriInfo uriInfo, final long restaurantId, final Collection<Pair<User, RestaurantRoleLevel>> pairs) {
        return pairs.stream().map(p -> from(uriInfo, restaurantId, p)).collect(Collectors.toList());
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public URI getUserUrl() {
        return userUrl;
    }

    public void setUserUrl(URI userUrl) {
        this.userUrl = userUrl;
    }

    public URI getRestaurantUrl() {
        return restaurantUrl;
    }

    public void setRestaurantUrl(URI restaurantUrl) {
        this.restaurantUrl = restaurantUrl;
    }
}
