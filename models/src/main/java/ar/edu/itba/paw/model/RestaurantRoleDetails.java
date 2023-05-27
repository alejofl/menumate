package ar.edu.itba.paw.model;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_role_details")
@Immutable
@IdClass(RestaurantRole.RestaurantRoleId.class)
public class RestaurantRoleDetails {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Id
    @Column(name = "restaurant_id", nullable = false, updatable = false)
    private long restaurantId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", insertable = false, updatable = false)
    private Restaurant restaurant;

    @Enumerated
    @Column(name = "role_level", nullable = false)
    private RestaurantRoleLevel level;

    @Column(name = "inprogress_order_count")
    private int pendingOrderCount;

    RestaurantRoleDetails() {

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

    public int getPendingOrderCount() {
        return pendingOrderCount;
    }
}
