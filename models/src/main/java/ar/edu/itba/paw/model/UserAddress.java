package ar.edu.itba.paw.model;

import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user_addresses")
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_addresses_address_id_seq")
    @SequenceGenerator(sequenceName = "user_addresses_address_id_seq", name = "user_addresses_address_id_seq", allocationSize = 1)
    @Column(name = "address_id", nullable = false, updatable = false)
    private long addressId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(nullable = false)
    private String address;

    @Column
    private String name;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;

    @Formula("name IS NULL")
    private boolean isNameNull;

    UserAddress() {

    }

    public UserAddress(long userId, String address, String name, LocalDateTime lastUsed) {
        this.userId = userId;
        this.address = address;
        this.name = name;
        this.lastUsed = lastUsed;
    }

    public long getAddressId() {
        return addressId;
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

    public void setAddress(String address) {
        this.address = address;
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

    @Override
    public int hashCode() {
        return Objects.hash(addressId, userId, address, name, lastUsed);
    }
}
