package ar.edu.itba.paw.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokens_token_id_seq")
    @SequenceGenerator(sequenceName = "tokens_token_id_seq", name = "tokens_token_id_seq", allocationSize = 1)
    @Column(name = "token_id", nullable = false, updatable = false)
    private long tokenId;

    @Column(length = 32, nullable = false)
    private String token;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.ORDINAL)
    private TokenType type;

    Token() {

    }

    public Token(User user, TokenType type, String token, LocalDateTime expiryDate) {
        this.user = user;
        this.type = type;
        this.token = token;
        this.expiryDate = expiryDate;
    }

    public long getTokenId() {
        return tokenId;
    }

    public void setTokenId(long tokenId) {
        this.tokenId = tokenId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public boolean isFresh() {
        return expiryDate.isAfter(LocalDateTime.now());
    }

    public boolean isExpired() {
        return !isFresh();
    }

    public boolean hasSameType(TokenType tokenType) {
        return this.type == tokenType;
    }
}
