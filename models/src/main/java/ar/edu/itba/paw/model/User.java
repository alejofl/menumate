package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_user_id_seq")
    @SequenceGenerator(sequenceName = "users_user_id_seq", name = "users_user_id_seq", allocationSize = 1)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(nullable = false)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "date_joined", insertable = false, updatable = false)
    private LocalDateTime dateJoined;

    @Column(name = "image_id")
    private Long imageId;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "preferred_language", nullable = false)
    private String preferredLanguage;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @OrderBy("(name IS NULL) ASC, lastUsed DESC")
    private List<UserAddress> addresses;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private UserRole role;

    User() {

    }

    public User(String email, String password, String name, LocalDateTime dateJoined, Long imageId, boolean isActive, String preferredLanguage) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.dateJoined = dateJoined;
        this.imageId = imageId;
        this.isActive = isActive;
        this.preferredLanguage = preferredLanguage;
        addresses = Collections.emptyList();
    }

    public Long getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public List<UserAddress> getAddresses() {
        return addresses;
    }

    public void setAddress(List<UserAddress> addresses) {
        this.addresses = addresses;
    }

    public UserRole getRole() {
        return role;
    }
}
