package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class AuthAnywhereFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER_TYPE = "Basic";

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(AUTH_HEADER_TYPE)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String credsBase64 = authHeader.substring(AUTH_HEADER_TYPE.length() + 1).trim();
            byte[] credsBytes = Base64.decode(credsBase64.getBytes(StandardCharsets.UTF_8));
            String credsDecoded = new String(credsBytes, StandardCharsets.UTF_8);

            int indexOfColon = credsDecoded.indexOf(':');
            String email = credsDecoded.substring(0, indexOfColon);
            String password = credsDecoded.substring(indexOfColon + 1);

            final Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            Optional<User> maybeUser = userService.getByEmail(email);
            if (maybeUser.isPresent()) {
                response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + jwtTokenUtil.generateAccessToken(maybeUser.get()));
            }
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.addHeader("WWW-Authenticate", "Basic realm=\"MenuMate\"");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
