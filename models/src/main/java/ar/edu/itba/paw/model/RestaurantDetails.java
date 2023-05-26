package ar.edu.itba.paw.model;

import javax.persistence.*;

@Entity
@Table(name = "restaurant_details")
public class RestaurantDetails {

    @Id
    @Column(name = "restaurant_id")
    private long restaurantId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id")
    private Restaurant restaurant;

    @Column(name = "average_rating")
    private int averageRating;

    @Column(name = "review_count")
    private int reviewCount;

    @Column(name = "average_price")
    private float averageProductPrice;

    RestaurantDetails() {

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
