package ar.edu.itba.paw.model;

public class Restaurant {
    private long restaurantId;
    private String name;
    private long logoId;
    private long portraitId1;
    private long portraitId2;
    private String address;
    private String description;
    private Boolean isActive;

    public Restaurant(long restaurantId, String name){
        this.restaurantId = restaurantId;
        this.name = name;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLogoId() {
        return logoId;
    }

    public void setLogoId(long logoId) {
        this.logoId = logoId;
    }

    public long getPortraitId1() {
        return portraitId1;
    }

    public void setPortraitId1(long portraitId1) {
        this.portraitId1 = portraitId1;
    }

    public long getPortraitId2() {
        return portraitId2;
    }

    public void setPortraitId2(long portraitId2) {
        this.portraitId2 = portraitId2;
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

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
