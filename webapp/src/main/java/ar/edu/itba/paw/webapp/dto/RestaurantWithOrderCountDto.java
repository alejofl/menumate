package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Restaurant;

import javax.ws.rs.core.UriInfo;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantWithOrderCountDto extends RestaurantDto {
    private int pendingOrderCount;

    public static RestaurantWithOrderCountDto from(final UriInfo uriInfo, final Restaurant restaurant) {
        final RestaurantWithOrderCountDto dto = new RestaurantWithOrderCountDto();
        fill(dto, uriInfo, restaurant);

        dto.pendingOrderCount = -1;

        return dto;
    }

    public static List<RestaurantWithOrderCountDto> fromCollection(final UriInfo uriInfo, final Collection<Restaurant> restaurants) {
        return restaurants.stream().map(r -> from(uriInfo, r)).collect(Collectors.toList());
    }
}
