package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RestaurantRoleDto {

    private long userId;
    private String role;

    private URI selfUrl;
    private URI userUrl;
    private URI restaurantUrl;

    public static RestaurantRoleDto from(final UriInfo uriInfo, final long restaurantId, final long userId, RestaurantRoleLevel level) {
        final RestaurantRoleDto dto = new RestaurantRoleDto();
        dto.userId = userId;
        dto.role = level.getMessageCode();

        dto.selfUrl = UriUtils.getRestaurantEmployeeUri(uriInfo, restaurantId, userId);
        dto.userUrl = UriUtils.getUserUri(uriInfo, userId);
        dto.restaurantUrl = UriUtils.getRestaurantUri(uriInfo, restaurantId);

        return dto;
    }

    public static RestaurantRoleDto from(final UriInfo uriInfo, final long restaurantId, final Pair<User, RestaurantRoleLevel> pair) {
        return from(uriInfo, restaurantId, pair.getKey().getUserId(), pair.getValue());
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
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

    @Override
    public int hashCode() {
        return Objects.hash(userId, role);
    }
}
