package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id")
    private Long userId;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String name;

    @Column(name = "date_joined", insertable = false)
    private LocalDateTime dateJoined;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "preferred_language")
    private String preferredLanguage;

    User() {

    }

    public User(Long userId, String email, String password, String name, LocalDateTime dateJoined, Long imageId, boolean isActive, String preferredLanguage) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.dateJoined = dateJoined;
        this.imageId = imageId;
        this.isActive = isActive;
        this.preferredLanguage = preferredLanguage;
    }

    public User(long userId, String email, String name, Long imageId, boolean isActive, String preferredLanguage) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.imageId = imageId;
        this.isActive = isActive;
        this.preferredLanguage = preferredLanguage;
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getDateJoined() {
        return dateJoined;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }
}
