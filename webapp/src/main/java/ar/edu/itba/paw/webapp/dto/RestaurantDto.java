package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantTags;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RestaurantDto {
    private long restaurantId;
    private String name;
    private String email;
    private String specialty;
    private long ownerUserId;
    private LocalDateTime dateCreated;
    private String address;
    private String description;
    private int maxTables;
    private Long logoId;
    private Long portrait1Id;
    private Long portrait2Id;
    private boolean isActive;
    private boolean deleted;
    private List<String> tags;

    private URI selfUrl;
    private URI ownerUrl;
    private URI logoUrl;
    private URI portrait1Url;
    private URI portrait2Url;
    private URI categoriesUrl;
    private URI ordersUrl;
    private URI reviewsUrl;
    private URI employeesUrl;

    protected static void fill(final RestaurantDto dto, final UriInfo uriInfo, final Restaurant restaurant) {
        dto.restaurantId = restaurant.getRestaurantId();
        dto.name = restaurant.getName();
        dto.specialty = restaurant.getSpecialty().getMessageCode();
        dto.ownerUserId = restaurant.getOwnerUserId();
        dto.dateCreated = restaurant.getDateCreated();
        dto.address = restaurant.getAddress();
        dto.description = restaurant.getDescription();
        dto.maxTables = restaurant.getMaxTables();
        dto.logoId = restaurant.getLogoId();
        dto.portrait1Id = restaurant.getPortrait1Id();
        dto.portrait2Id = restaurant.getPortrait2Id();
        dto.isActive = restaurant.getIsActive();
        dto.deleted = restaurant.getDeleted();
        dto.tags = restaurant.getTags().stream().map(RestaurantTags::getMessageCode).collect(Collectors.toList());

        dto.selfUrl = UriUtils.getRestaurantUri(uriInfo, restaurant.getRestaurantId());
        dto.ownerUrl = UriUtils.getUserUri(uriInfo, restaurant.getOwnerUserId());
        dto.logoUrl = UriUtils.getImageUri(uriInfo, restaurant.getLogoId());
        dto.portrait1Url = UriUtils.getImageUri(uriInfo, restaurant.getPortrait1Id());
        dto.portrait2Url = UriUtils.getImageUri(uriInfo, restaurant.getPortrait2Id());
        dto.categoriesUrl = UriUtils.getRestaurantCategoriesUri(uriInfo, restaurant.getRestaurantId());
        dto.ordersUrl = UriUtils.getOrdersByRestaurantUri(uriInfo, restaurant.getRestaurantId());
        dto.reviewsUrl = UriUtils.getReviewsByRestaurantUri(uriInfo, restaurant.getRestaurantId());
        dto.employeesUrl = UriUtils.getRestaurantEmployeesUri(uriInfo, restaurant.getRestaurantId());
    }

    public static RestaurantDto fromRestaurant(final UriInfo uriInfo, final Restaurant restaurant) {
        final RestaurantDto dto = new RestaurantDto();
        fill(dto, uriInfo, restaurant);
        return dto;
    }

    public static List<RestaurantDto> fromRestaurantCollection(final UriInfo uriInfo, final Collection<Restaurant> restaurants) {
        return restaurants.stream().map(r -> fromRestaurant(uriInfo, r)).collect(Collectors.toList());
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxTables() {
        return maxTables;
    }

    public void setMaxTables(int maxTables) {
        this.maxTables = maxTables;
    }

    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public Long getPortrait1Id() {
        return portrait1Id;
    }

    public void setPortrait1Id(Long portrait1Id) {
        this.portrait1Id = portrait1Id;
    }

    public Long getPortrait2Id() {
        return portrait2Id;
    }

    public void setPortrait2Id(Long portrait2Id) {
        this.portrait2Id = portrait2Id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getOwnerUrl() {
        return ownerUrl;
    }

    public void setOwnerUrl(URI ownerUrl) {
        this.ownerUrl = ownerUrl;
    }

    public URI getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(URI logoUrl) {
        this.logoUrl = logoUrl;
    }

    public URI getPortrait1Url() {
        return portrait1Url;
    }

    public void setPortrait1Url(URI portrait1Url) {
        this.portrait1Url = portrait1Url;
    }

    public URI getPortrait2Url() {
        return portrait2Url;
    }

    public void setPortrait2Url(URI portrait2Url) {
        this.portrait2Url = portrait2Url;
    }

    public URI getCategoriesUrl() {
        return categoriesUrl;
    }

    public void setCategoriesUrl(URI categoriesUrl) {
        this.categoriesUrl = categoriesUrl;
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

    public URI getEmployeesUrl() {
        return employeesUrl;
    }

    public void setEmployeesUrl(URI employeesUrl) {
        this.employeesUrl = employeesUrl;
    }
}
