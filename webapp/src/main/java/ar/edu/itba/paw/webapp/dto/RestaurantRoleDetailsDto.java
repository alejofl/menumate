package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.RestaurantRoleDetails;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantRoleDetailsDto {
    private long userId;
    private long restaurantId;
    private String role;
    private int pendingOrderCount;

    private URI selfUrl;
    private URI userUrl;
    private URI restaurantUrl;

    public static RestaurantRoleDetailsDto from(final UriInfo uriInfo, final RestaurantRoleDetails details) {
        final RestaurantRoleDetailsDto dto = new RestaurantRoleDetailsDto();
        dto.userId = details.getUserId();
        dto.restaurantId = details.getRestaurantId();
        dto.role = details.getLevel().getMessageCode();
        dto.pendingOrderCount = details.getPendingOrderCount();

        dto.selfUrl = UriUtils.getRestaurantEmployeeUri(uriInfo, dto.restaurantId, dto.userId);
        dto.userUrl = UriUtils.getUserUri(uriInfo, dto.userId);
        dto.restaurantUrl = UriUtils.getRestaurantUri(uriInfo, dto.restaurantId);

        return dto;
    }

    public static List<RestaurantRoleDetailsDto> fromCollection(final UriInfo uriInfo, final Collection<RestaurantRoleDetails> list) {
        return list.stream().map(p -> from(uriInfo, p)).collect(Collectors.toList());
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

    public int getPendingOrderCount() {
        return pendingOrderCount;
    }

    public void setPendingOrderCount(int pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
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
}
