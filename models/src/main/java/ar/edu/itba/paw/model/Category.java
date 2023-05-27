package ar.edu.itba.paw.model;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categories_category_id_seq")
    @SequenceGenerator(sequenceName = "categories_category_id_seq", name = "categories_category_id_seq", allocationSize = 1)
    @Column(name = "category_id", nullable = false, updatable = false)
    private Long categoryId;

    @Column(name = "restaurant_id", nullable = false, insertable = false, updatable = false)
    private long restaurantId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id", referencedColumnName = "restaurant_id", nullable = false, updatable = false)
    private Restaurant restaurant;

    @Column(nullable = false)
    private String name;

    @Column(name = "order_num", nullable = false)
    private int orderNum;

    @Column(nullable = false, insertable = false)
    private boolean deleted;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    @Where(clause = "deleted = false AND available = true")
    private List<Product> products;

    Category() {

    }

    public Category(Restaurant restaurant, String name, int orderNum) {
        this.restaurantId = restaurant.getRestaurantId();
        this.restaurant = restaurant;
        this.name = name;
        this.orderNum = orderNum;
    }

    // TODO: Remove this constructor, it remains for backwards compatibility until ORM migration is finished
    public Category(long categoryId, Restaurant restaurant, String name, int orderNum, boolean deleted) {
        this.categoryId = categoryId;
        this.restaurant = restaurant;
        this.name = name;
        this.orderNum = orderNum;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
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
