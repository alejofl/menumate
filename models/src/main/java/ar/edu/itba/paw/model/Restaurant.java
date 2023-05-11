package ar.edu.itba.paw.model;

public class Restaurant {
    private final long restaurantId;
    private final String name;
    private final String email;
    private final long ownerUserId;
    private final Long logoId;
    private final Long portraitId1;
    private final Long portraitId2;
    private final String address;
    private final String description;
    private final int maxTables;
    private final boolean isActive;

    public Restaurant(long restaurantId, String name, String email, long ownerUserId, Long logoId, Long portrait1Id, Long portrait2Id, String address, String description, int maxTables, boolean isActive) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.email = email;
        this.ownerUserId = ownerUserId;
        this.logoId = logoId;
        this.portraitId1 = portrait1Id;
        this.portraitId2 = portrait2Id;
        this.address = address;
        this.description = description;
        this.maxTables = maxTables;
        this.isActive = isActive;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public long getOwnerUserId() {
        return ownerUserId;
    }

    public Long getLogoId() {
        return logoId;
    }

    public Long getPortraitId1() {
        return portraitId1;
    }

    public Long getPortraitId2() {
        return portraitId2;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxTables() {
        return maxTables;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
