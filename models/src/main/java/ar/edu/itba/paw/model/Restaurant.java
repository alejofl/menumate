package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "restaurants_restaurant_id_seq")
    @SequenceGenerator(sequenceName = "restaurants_restaurant_id_seq", name = "restaurants_restaurant_id_seq", allocationSize = 1)
    @Column(name = "restaurant_id")
    private Long restaurantId;

    @Column
    private String name;

    @Column
    private String email;

    @Enumerated(EnumType.ORDINAL)
    private RestaurantSpecialty specialty;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "owner_user_id", referencedColumnName = "user_id")
    private User owner;
    
    @Column(name = "date_created", nullable = false, insertable = false, updatable = false)
    private LocalDateTime dateCreated;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String description;

    @Column(name = "max_tables", nullable = false)
    private int maxTables;

    @Column(name = "logo_id")
    private Long logoId;

    @Column(name = "portrait_1_id")
    private Long portrait1Id;

    @Column(name = "portrait_2_id")
    private Long portrait2Id;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(nullable = false)
    private boolean deleted;

    @ElementCollection(targetClass = RestaurantTags.class)
    @CollectionTable(name = "restaurant_tags", joinColumns = @JoinColumn(name = "restaurant_id"))
    @Column(name = "tag_id", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private List<RestaurantTags> tags;
    
    Restaurant() {
        
    }

    public Restaurant(String name, String email, RestaurantSpecialty specialty, User owner, String address, String description, int maxTables, Long logoId, Long portrait1Id, Long portrait2Id, boolean isActive, List<RestaurantTags> tags) {
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.owner = owner;
        this.address = address;
        this.description = description;
        this.maxTables = maxTables;
        this.logoId = logoId;
        this.portrait1Id = portrait1Id;
        this.portrait2Id = portrait2Id;
        this.isActive = isActive;
        this.tags = tags;
        this.deleted = false;
    }

    // TODO: REMOVE THIS CONSTRUCTOR, It's only here for backwards compatibility until ORM migration is finished.
    public Restaurant(long restaurantId, String name, String email, RestaurantSpecialty specialty, long ownerUserId, Long logoId, Long portrait1Id, Long portrait2Id, String address, String description, int maxTables, boolean isActive, boolean deleted) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.owner = new User(ownerUserId, "EMAIL", "PASSWORD", null, true, "en");
        this.address = address;
        this.description = description;
        this.maxTables = maxTables;
        this.logoId = logoId;
        this.portrait1Id = portrait1Id;
        this.portrait2Id = portrait2Id;
        this.isActive = isActive;
        this.tags = new ArrayList<>();
        tags.add(RestaurantTags.KARAOKE); // KARAOKE PARTY
        tags.add(RestaurantTags.KARAOKE); // KARAOKE PARTY
        tags.add(RestaurantTags.KARAOKE); // KARAOKE PARTY
        this.deleted = deleted;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RestaurantSpecialty getSpecialty() {
        return specialty;
    }

    public void setSpecialty(RestaurantSpecialty specialty) {
        this.specialty = specialty;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxTables() {
        return maxTables;
    }

    public void setMaxTables(int maxTables) {
        this.maxTables = maxTables;
    }

    public Long getLogoId() {
        return logoId;
    }

    public void setLogoId(Long logoId) {
        this.logoId = logoId;
    }

    public Long getPortrait1Id() {
        return portrait1Id;
    }

    public void setPortrait1Id(Long portrait1Id) {
        this.portrait1Id = portrait1Id;
    }

    public Long getPortrait2Id() {
        return portrait2Id;
    }

    public void setPortrait2Id(Long portrait2Id) {
        this.portrait2Id = portrait2Id;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public List<RestaurantTags> getTags() {
        return tags;
    }
}
