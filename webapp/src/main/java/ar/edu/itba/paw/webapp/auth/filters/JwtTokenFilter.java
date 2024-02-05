package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.JwtTokenDetails;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private static final String AUTH_HEADER_TYPE = "Bearer";

    private final JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Autowired
    public JwtTokenFilter(final JwtTokenUtil jwtTokenUtil, final UserDetailsService userDetailsService, UserService userService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // Get authorization header and validate
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(header) || !header.startsWith(AUTH_HEADER_TYPE)) {
            chain.doFilter(request, response);
            return;
        }

        // Get jwt token and validate
        final String token = header.substring(AUTH_HEADER_TYPE.length() + 1).trim();
        final JwtTokenDetails jwtDetails = jwtTokenUtil.validate(token);
        if (jwtDetails == null) {
            response.addHeader("WWW-Authenticate", "Bearer realm=\"MenuMate\"");
            chain.doFilter(request, response);
            return;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(jwtDetails.getEmail());
        if (userDetails == null) {
            response.addHeader("WWW-Authenticate", "Bearer realm=\"MenuMate\"");
            chain.doFilter(request, response);
            return;
        }

        // Check if refresh token was sent to refresh tokens
        if (jwtDetails.getTokenType().isRefreshToken()) {
            final Optional<User> maybeUser = userService.getByEmail(userDetails.getUsername());
            if (maybeUser.isPresent()) {
                final User user = maybeUser.get();
                final ServletUriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath(request);
                response.setHeader("X-MenuMate-AuthToken", jwtTokenUtil.generateAccessToken(uriBuilder, user));
                response.setHeader("X-MenuMate-RefreshToken", jwtTokenUtil.generateRefreshToken(uriBuilder, user));
            }
        }

        // Get user identity and set it on the spring security context
        final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
