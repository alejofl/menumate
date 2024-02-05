package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.TokenService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
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
            final String credsBase64 = authHeader.substring(AUTH_HEADER_TYPE.length() + 1).trim();
            final byte[] credsBytes = Base64.decode(credsBase64.getBytes(StandardCharsets.UTF_8));
            final String credsDecoded = new String(credsBytes, StandardCharsets.UTF_8);

            int indexOfColon = credsDecoded.indexOf(':');
            final String email = credsDecoded.substring(0, indexOfColon);
            final String credentials = credsDecoded.substring(indexOfColon + 1);

            final Optional<User> maybeUser = userService.getByEmail(email);
            if (maybeUser.isPresent()) {
                final User user = maybeUser.get();
                final Optional<Token> maybeToken = tokenService.getByToken(credentials);
                if (maybeToken.isPresent()) {
                    final Token tkn = maybeToken.get();
                    if (tkn.getUser().getUserId().longValue() != user.getUserId()) {
                        throw new BadCredentialsException("exception.BadCredentialsException.token");
                    }
                    userService.verifyUser(tkn.getToken());

                    final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
                    final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else if (!user.getIsActive()) {
                    userService.sendVerificationToken(user.getEmail());
                    throw new UserNotVerifiedException(user);
                } else {
                    final Authentication auth = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, credentials)
                    );
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
                final ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath(request);
                response.setHeader("X-MenuMate-AuthToken", jwtTokenUtil.generateAccessToken(uriBuilder, user));
                response.setHeader("X-MenuMate-RefreshToken", jwtTokenUtil.generateRefreshToken(uriBuilder, user));
            }
        } catch (Exception e) {
            response.addHeader("WWW-Authenticate", "Basic realm=\"MenuMate\"");
        }

        filterChain.doFilter(request, response);
    }
}
