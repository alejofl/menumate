package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.RestaurantRoleDetails;

import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantWithOrderCountDto extends RestaurantDto {
    private int pendingOrderCount;

    public static RestaurantWithOrderCountDto from(final UriInfo uriInfo, final RestaurantRoleDetails roleDetails) {
        final RestaurantWithOrderCountDto dto = new RestaurantWithOrderCountDto();
        fill(dto, uriInfo, roleDetails.getRestaurant());

        dto.pendingOrderCount = roleDetails.getPendingOrderCount();

        return dto;
    }

    public static List<RestaurantWithOrderCountDto> fromCollection(final UriInfo uriInfo, final Collection<RestaurantRoleDetails> restaurants) {
        return restaurants.stream().map(r -> from(uriInfo, r)).collect(Collectors.toList());
    }

    public int getPendingOrderCount() {
        return pendingOrderCount;
    }

    public void setPendingOrderCount(int pendingOrderCount) {
        this.pendingOrderCount = pendingOrderCount;
    }
}
