package ar.edu.itba.paw.webapp.api;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnconditionalCacheFilter extends OncePerRequestFilter {

    public static final int MAX_TIME = 31536000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getMethod().equals("GET")) {
            response.setHeader("Cache-Control", String.format("public, max-age=%d", MAX_TIME));
        }
        filterChain.doFilter(request, response);
    }
}
