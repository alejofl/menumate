package ar.edu.itba.paw.model;

public class User {

    private final int userId;
    private final String email;
    private final String name;
    private final int imageId;
    private final boolean isActive;

    public User(int userId, String email, String name, int imageId, boolean isActive) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.imageId = imageId;
        this.isActive = isActive;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public int getUserId() {
        return userId;
    }

    public int getImageId() {
        return imageId;
    }

    public boolean getIsActive() {
        return isActive;
    }
}
