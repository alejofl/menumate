package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_category_id_seq")
    @SequenceGenerator(sequenceName = "categories_category_id_seq", name = "categories_category_id_seq", allocationSize = 1)
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "restaurant_id", nullable = false, insertable = false, updatable = false)
    private long restaurantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int order;

    @Column(nullable = false, insertable = false)
    private boolean deleted;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products;

    Category() {

    }

    public Category(Restaurant restaurant, String name, int order) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurant = restaurant;
        this.name = name;
        this.order = order;
    }

    // TODO: Remove this constructor, it remains for backwards compatibility until ORM migration is finished
    public Category(long categoryId, Restaurant restaurant, String name, int order, boolean deleted) {
        this.categoryId = categoryId;
        this.restaurant = restaurant;
        this.name = name;
        this.order = order;
        this.deleted = deleted;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public long getRestaurantId() {
        return restaurantId;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurant = restaurant;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<Product> getProducts() {
        return products;
    }
}
