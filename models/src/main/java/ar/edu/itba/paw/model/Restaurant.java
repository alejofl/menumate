package ar.edu.itba.paw.model;

public class Restaurant {
    private final int restaurantId;
    private final String name;
    private final String email;
    private int logoId;
    private int portraitId1;
    private int portraitId2;
    private String address;
    private String description;
    private Boolean isActive;

    public Restaurant(int restaurantId, String name, String email) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.email = email;
    }

    public Restaurant(int restaurantId, String name, String email, int logoId, int portrait1Id, int portrait2Id, String address, String description, Boolean isActive) {
        this(restaurantId, name, email);
        this.logoId = logoId;
        this.portraitId1 = portrait1Id;
        this.portraitId2 = portrait2Id;
        this.address = address;
        this.description = description;
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

    public Boolean getActive() {
        return isActive;
    }
}
