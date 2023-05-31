package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_addresses")
@IdClass(UserAddress.UserAddressId.class)
public class UserAddress {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Id
    @Column(nullable = false, updatable = false)
    private String address;

    @Column
    private String name;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;

    UserAddress() {

    }

    public UserAddress(long userId, String address, String name, LocalDateTime lastUsed) {
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.lastUsed = lastUsed;
    }

    public long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(LocalDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public static class UserAddressId implements Serializable {
        private long userId;
        private String address;

        UserAddressId() {

        }

        public UserAddressId(long userId, String address) {
            this.userId = userId;
            this.address = address;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RestaurantRole.RestaurantRoleId)) return false;
            UserAddress.UserAddressId oi = (UserAddress.UserAddressId) o;
            return userId == oi.userId && address.equals(oi.address);
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, address);
        }
    }
}
