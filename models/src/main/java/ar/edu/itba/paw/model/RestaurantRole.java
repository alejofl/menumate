package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "restaurant_roles")
@IdClass(RestaurantRole.RestaurantRoleId.class)
public class RestaurantRole {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Id
    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private long restaurantId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", insertable = false, updatable = false)
    private Restaurant restaurant;

    @Enumerated
    @Column(name = "role_level", nullable = false)
    private RestaurantRoleLevel level;

    RestaurantRole() {

    }

    public RestaurantRole(long userId, long restaurantId, RestaurantRoleLevel level) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.level = level;
    }

    public long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public RestaurantRoleLevel getLevel() {
        return level;
    }

    public void setLevel(RestaurantRoleLevel level) {
        this.level = level;
    }

    public static class RestaurantRoleId implements Serializable {
        private long userId;
        private long restaurantId;

        RestaurantRoleId() {

        }

        public RestaurantRoleId(long userId, long restaurantId) {
            this.userId = userId;
            this.restaurantId = restaurantId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof RestaurantRole.RestaurantRoleId)) return false;
            RestaurantRole.RestaurantRoleId oi = (RestaurantRole.RestaurantRoleId) o;
            return userId == oi.userId && restaurantId == oi.restaurantId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(userId, restaurantId);
        }
    }
}
