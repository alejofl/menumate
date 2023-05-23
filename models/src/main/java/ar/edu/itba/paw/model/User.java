package ar.edu.itba.paw.model;

public class User {

    private final long userId;
    private final String email;
    private final String name;
    private final Long imageId;
    private final boolean isActive;
    private final String preferredLanguage;

    public User(long userId, String email, String name, Long imageId, boolean isActive, String preferredLanguage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.imageId = imageId;
        this.isActive = isActive;
        this.preferredLanguage = preferredLanguage;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public long getUserId() {
        return userId;
    }

    public Long getImageId() {
        return imageId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }
}
