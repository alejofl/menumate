package ar.edu.itba.paw.webapp.filter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnconditionalCacheFilter extends OncePerRequestFilter {

    private static final int STATIC_FILES_MAX_AGE = 31536000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(HttpMethod.GET.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.CACHE_CONTROL, String.format("public, max-age=%d, immutable", STATIC_FILES_MAX_AGE));
        }

        filterChain.doFilter(request, response);
    }
}