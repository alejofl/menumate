package ar.edu.itba.paw.webapp.auth;

import java.util.Date;

public class JwtTokenDetails {

    private final String id;
    private final String token;
    private final String email;
    private final Date issuedDate;
    private final Date expirationDate;
    private final JwtTokenType tokenType;

    private JwtTokenDetails(String id, String token, String email, Date issuedDate, Date expirationDate, JwtTokenType tokenType) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.issuedDate = issuedDate;
        this.expirationDate = expirationDate;
        this.tokenType = tokenType;
    }

    public String getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getEmail() {
        return email;
    }

    public Date getIssuedDate() {
        return issuedDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public JwtTokenType getTokenType() {
        return tokenType;
    }

    public static class Builder {

        private String id;
        private String token;
        private String email;
        private Date issuedDate;
        private Date expirationDate;
        private JwtTokenType tokenType;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setIssuedDate(Date issuedDate) {
            this.issuedDate = issuedDate;
            return this;
        }

        public Builder setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public Builder setToken(String token) {
            this.token = token;
            return this;
        }

        public Builder setTokenType(JwtTokenType tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public JwtTokenDetails build() {
            return new JwtTokenDetails(id, token, email, issuedDate, expirationDate, tokenType);
        }
    }
}