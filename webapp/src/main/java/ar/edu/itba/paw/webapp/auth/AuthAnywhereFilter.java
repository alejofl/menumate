package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.TokenService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.exception.UserNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

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

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserDetailsService userDetailsService;

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
            String credentials = credsDecoded.substring(indexOfColon + 1);

            Optional<User> maybeUser = userService.getByEmail(email);
            if (maybeUser.isPresent()) {
                User user = maybeUser.get();

                Optional<Token> maybeToken = tokenService.getByToken(credentials);

                if (maybeToken.isPresent()) {
                    Token tkn = maybeToken.get();
                    if (tkn.getUser().getUserId().longValue() != user.getUserId()) {
                        throw new BadCredentialsException("Token doesn't belong to user");
                    }
                    userService.verifyUser(tkn.getToken());

                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (!user.getIsActive()) {
                    userService.sendVerificationToken(user.getEmail());
                    throw new UserNotVerifiedException("User not verified", user);
                } else {
                    final Authentication auth = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, credentials)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath(request);
                response.setHeader("X-MenuMate-AuthToken", jwtTokenUtil.generateAccessToken(uriBuilder, user));
            }
        } catch (Exception e) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"MenuMate\"");
        }

        filterChain.doFilter(request, response);
    }
}
