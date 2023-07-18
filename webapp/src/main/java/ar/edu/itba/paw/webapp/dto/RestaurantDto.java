package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Restaurant;
import ar.edu.itba.paw.model.RestaurantTags;

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

    public static RestaurantDto fromRestaurant(final Restaurant restaurant) {
        final RestaurantDto dto = new RestaurantDto();
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

        return dto;
    }

    public static List<RestaurantDto> fromRestaurantCollection(final Collection<Restaurant> restaurants) {
        return restaurants.stream().map(RestaurantDto::fromRestaurant).collect(Collectors.toList());
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
}
