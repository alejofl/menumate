package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_resetpassword_tokens")
public class UserResetpasswordToken {

    @Id
    @Column(name = "user_id", nullable = false, updatable = false)
    private long userId;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @PrimaryKeyJoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(length = 32, nullable = false)
    private String token;

    @Column(nullable = false)
    private LocalDateTime expires;

    UserResetpasswordToken() {

    }

    public UserResetpasswordToken(User user, String token, LocalDateTime expires) {
        this.userId = user.getUserId();
        this.user = user;
        this.token = token;
        this.expires = expires;
    }

    public long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpires() {
        return expires;
    }

    public void setExpires(LocalDateTime expires) {
        this.expires = expires;
    }

    public boolean isFresh() {
        return expires.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return !isFresh();
    }
}
