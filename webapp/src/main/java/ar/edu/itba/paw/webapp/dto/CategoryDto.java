package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Category;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CategoryDto {
    private long categoryId;
    private String name;
    private int orderNum;
    private boolean deleted;

    private URI selfUrl;
    private URI restaurantUrl;
    private URI productsUrl;

    public static CategoryDto fromCategory(final UriInfo uriInfo, final Category category) {
        final CategoryDto dto = new CategoryDto();

        dto.name = category.getName();
        dto.orderNum = category.getOrderNum();
        dto.deleted = category.getDeleted();

        dto.selfUrl = UriUtils.getCategoryUri(uriInfo, category);
        dto.restaurantUrl = UriUtils.getRestaurantUri(uriInfo, category.getRestaurantId());
        dto.productsUrl = UriUtils.getCategoryProductsUri(uriInfo, category);

        return dto;
    }

    public static List<CategoryDto> fromCategoryCollection(final UriInfo uriInfo, final Collection<Category> categories) {
        return categories.stream().map(c -> fromCategory(uriInfo, c)).collect(Collectors.toList());
    }

    public boolean isDeleted() {
        return deleted;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getRestaurantUrl() {
        return restaurantUrl;
    }

    public void setRestaurantUrl(URI restaurantUrl) {
        this.restaurantUrl = restaurantUrl;
    }

    public URI getProductsUrl() {
        return productsUrl;
    }

    public void setProductsUrl(URI productsUrl) {
        this.productsUrl = productsUrl;
    }
}
