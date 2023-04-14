package ar.edu.itba.paw.model;

public class Restaurant {
    private final long restaurantId;
    private final String name;
    private final String email;
    private long logoId;
    private long portraitId1;
    private long portraitId2;
    private String address;
    private String description;
    private Boolean isActive;

    public Restaurant(long restaurantId, String name, String email) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.email = email;
    }

    public Restaurant(long restaurantId, String name, String email, long logoId, long portrait1Id, long portrait2Id, String address, String description, Boolean isActive) {
        this(restaurantId, name, email);
        this.logoId = logoId;
        this.portraitId1 = portrait1Id;
        this.portraitId2 = portrait2Id;
        this.address = address;
        this.description = description;
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

    public long getLogoId() {
        return logoId;
    }

    public long getPortraitId1() {
        return portraitId1;
    }

    public long getPortraitId2() {
        return portraitId2;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getActive() {
        return isActive;
    }
}
