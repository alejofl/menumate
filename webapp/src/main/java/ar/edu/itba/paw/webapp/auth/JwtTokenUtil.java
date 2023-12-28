package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

public class JwtTokenUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(JwtTokenUtil.class);
    private final static long EXPIRATION_TIME_MILLIS = 7 * 24 * 60 * 60 * 1000; // 1 week

    private final UserDetailsService userDetailsService;
    private final Key jwtSecretKey;

    public JwtTokenUtil(UserDetailsService userDetailsService, Resource jwtKeyRes) throws IOException {
        this.userDetailsService = userDetailsService;
        jwtSecretKey = Keys.hmacShaKeyFor(FileCopyUtils.copyToString(new InputStreamReader(jwtKeyRes.getInputStream())).getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String uriInfo, User user) {
        Claims claims = Jwts.claims();
        claims.put("name", user.getName());

        if (user.hasRole()) {
            claims.put("role", user.getRole().getLevel());
        }

        String selfUrl = new StringBuilder().append(uriInfo).append(UriUtils.USERS_URL).append(user.getUserId()).toString();
        claims.put("selfUrl", selfUrl);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILLIS))
                .setClaims(claims)
                .signWith(jwtSecretKey)
                .compact();
    }

    public UserDetails validateAndLoad(String token) {
        try {
            Jws<Claims> parsed = Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            Claims claims = parsed.getBody();

            if (claims.getExpiration().before(new Date(System.currentTimeMillis())))
                return null;

            String email = claims.getSubject();
            return userDetailsService.loadUserByUsername(email);
        } catch (SignatureException ex) {
            LOGGER.warn("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            LOGGER.warn("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            LOGGER.warn("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            LOGGER.warn("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            LOGGER.warn("JWT claims string is empty - {}", ex.getMessage());
        }

        return null;
    }
}
