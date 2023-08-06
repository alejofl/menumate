package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.auth.AuthAnywhereFilter;
import ar.edu.itba.paw.webapp.auth.JwtTokenFilter;
import ar.edu.itba.paw.webapp.auth.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan("ar.edu.itba.paw.webapp.auth")
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccessValidator accessValidator;

    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Autowired
    private AuthAnywhereFilter authAnywhereFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/images/{imageId:\\d+}").permitAll()

                .antMatchers(HttpMethod.GET, "/restaurants").permitAll()
                .antMatchers(HttpMethod.POST, "/restaurants").permitAll()
                .antMatchers(HttpMethod.GET, "/restaurants/{restaurantId:\\d+}").permitAll()
                .antMatchers(HttpMethod.PUT, "/restaurants/{restaurantId:\\d+}").access("@accessValidator.checkRestaurantAdmin(#restaurantId)")
                .antMatchers(HttpMethod.DELETE, "/restaurants/{restaurantId:\\d+}").access("hasRole('MODERATOR') or @accessValidator.checkRestaurantOwner(#restaurantId)")

                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{userId:\\d+}").permitAll()
                .antMatchers("/users/{userId:\\d+}/**").access("@accessValidator.checkIsUser(#userId)")
                .antMatchers("/users/verification-tokens/**").permitAll()
                .antMatchers("/users/resetpassword-tokens/**").permitAll()

                .antMatchers(HttpMethod.GET, "/orders").authenticated()
                .antMatchers(HttpMethod.GET, "/orders/{orderId:\\d+}").access("@accessValidator.checkOrderOwnerOrHandler(#orderId)")

                .antMatchers(HttpMethod.GET, "/reviews/{orderId:\\d+}/**").permitAll()
                .antMatchers(HttpMethod.PUT, "/reviews/{orderId:\\d+}").access("@accessValidator.checkOrderOwner(#orderId)")
                .antMatchers(HttpMethod.DELETE, "/reviews/{orderId:\\d+}").access("@accessValidator.checkOrderOwner(#orderId)")
                .antMatchers(HttpMethod.PUT, "/reviews/{orderId:\\d+}/reply").access("@accessValidator.checkOrderHandler(#orderId)")
                .antMatchers(HttpMethod.DELETE, "/reviews/{orderId:\\d+}/reply").access("@accessValidator.checkOrderHandler(#orderId)")

                .antMatchers("/**").permitAll()

                .and().exceptionHandling().
                authenticationEntryPoint((request, response, ex) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
                })

                .and()
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(authAnywhereFilter, UsernamePasswordAuthenticationFilter.class)

                // Enable CORS and disable csrf rules
                .cors().and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers("/static/**");
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        // TODO: Fill allowed origins, headers and methods for CORS.
        config.setAllowedOrigins(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedHeaders(Collections.singletonList(CorsConfiguration.ALL));
        config.setAllowedMethods(Collections.singletonList(CorsConfiguration.ALL));
        // TODO: config.setExposedHeaders(...);
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public JwtTokenUtil jwtTokenUtil(@Value("classpath:jwtSecret.key") Resource jwtKeyRes) throws IOException {
        return new JwtTokenUtil(userDetailsService, jwtKeyRes);
    }
}
