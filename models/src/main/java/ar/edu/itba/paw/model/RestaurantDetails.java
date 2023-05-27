package ar.edu.itba.paw.model;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_details")
@Immutable
public class RestaurantDetails {

    @Id
    @Column(name = "restaurant_id", nullable = false)
    private long restaurantId;

    @OneToOne(fetch = FetchType.EAGER, optional = false)
    @PrimaryKeyJoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "average_rating", nullable = false)
    private int averageRating;

    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Column(name = "average_price", nullable = false)
    private float averageProductPrice;

    RestaurantDetails() {

    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public int getAverageRating() {
        return averageRating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public float getAverageProductPrice() {
        return averageProductPrice;
    }
}
