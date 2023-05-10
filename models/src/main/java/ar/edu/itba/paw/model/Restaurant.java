package ar.edu.itba.paw.model;

public class Restaurant {
    private final int restaurantId;
    private final String name;
    private final String email;
    private final int ownerUserId;
    private final int logoId;
    private final int portraitId1;
    private final int portraitId2;
    private final String address;
    private final String description;
    private final int maxTables;
    private final boolean isActive;

    public Restaurant(int restaurantId, String name, String email, int ownerUserId, int logoId, int portrait1Id, int portrait2Id, String address, String description, int maxTables, boolean isActive) {
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

    public int getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public int getLogoId() {
        return logoId;
    }

    public int getPortraitId1() {
        return portraitId1;
    }

    public int getPortraitId2() {
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
