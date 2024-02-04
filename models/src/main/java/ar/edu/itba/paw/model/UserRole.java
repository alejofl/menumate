package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "user_roles")
public class UserRole {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Enumerated
    @Column(name = "role_level", nullable = false)
    private UserRoleLevel level;

    UserRole() {

    }

    public UserRole(long userId, UserRoleLevel level) {
        this.userId = userId;
        this.level = level;
    }

    public long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public UserRoleLevel getLevel() {
        return level;
    }

    public void setLevel(UserRoleLevel level) {
        this.level = level;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, level);
    }
}
