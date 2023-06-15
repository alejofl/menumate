package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.auth.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.FileCopyUtils;

import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@ComponentScan("ar.edu.itba.paw.webapp.auth")
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Value("classpath:remembermekey.txt")
    private Resource remembermeKey;

    @Autowired
    private AccessValidator accessValidator;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

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
                // Login logic
                .and().formLogin().failureHandler(customAuthenticationEntryPoint)
                .loginPage("/auth/login")
                .usernameParameter("email").passwordParameter("password")
                .defaultSuccessUrl("/", false)

                // Logout logic
                .and().logout()
                .logoutUrl("/auth/logout")
                .logoutSuccessUrl("/")

                // Remember me logic
                .and().rememberMe()
                .rememberMeParameter("rememberme")
                .userDetailsService(userDetailsService)
                .key(FileCopyUtils.copyToString(new InputStreamReader(remembermeKey.getInputStream())))
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))

                // Request authorization
                .and().authorizeRequests()

                // Static content & images
                .antMatchers("/static/**").permitAll()
                .antMatchers("/images/{id:\\d+}").permitAll()

                // General public pages
                .antMatchers("/").permitAll()
                .antMatchers("/403").permitAll()
                .antMatchers(HttpMethod.GET, "/restaurants").permitAll()

                // Authentication pages
                .antMatchers("/auth/**").permitAll()

                // User pages
                .antMatchers(HttpMethod.GET, "/user/**").authenticated()
                .antMatchers(HttpMethod.POST, "/user/addresses/**").authenticated()
                
                // Restaurant public pages
                .antMatchers(HttpMethod.GET, "/restaurants/{restaurant_id:\\d+}").permitAll()
                .antMatchers(HttpMethod.POST, "/restaurants/{restaurant_id:\\d+}/orders").permitAll()

                // Restaurants edit pages
                .antMatchers("/restaurants/{restaurant_id:\\d+}/edit/**").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantAdmin(#restaurant_id)")
                .antMatchers(HttpMethod.POST, "/restaurants/{restaurant_id:\\d+}/employees/**").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantOwner(#restaurant_id)")
                .antMatchers(HttpMethod.POST, "/restaurants/{restaurant_id:\\d+}/categories/**").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantAdmin(#restaurant_id)")
                .antMatchers(HttpMethod.POST, "/restaurants/{restaurant_id:\\d+}/products/**").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantAdmin(#restaurant_id)")
                .antMatchers(HttpMethod.POST, "/restaurants/{restaurant_id:\\d+}/promotions/**").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantAdmin(#restaurant_id)")
                .antMatchers("/restaurants/{restaurant_id:\\d+}/reviews").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantAdmin(#restaurant_id)")
                // Restaurant orders pages
                .antMatchers(HttpMethod.GET, "/restaurants/{restaurant_id:\\d+}/orders/**").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkRestaurantOrderHandler(#restaurant_id)")

                // Create restaurant pages
                .antMatchers("/restaurants/create").authenticated()

                // Orders pages
                .antMatchers(HttpMethod.GET, "/orders/{order_id:\\d+}").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkOrderOwner(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/{status:confirm|ready|deliver|cancel}").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkOrderHandler(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/review").access("hasRole('ROLE_MODERATOR') or @accessValidator.checkOrderOwner(#order_id)")

                .and().exceptionHandling()
                .accessDeniedPage("/403")

                // Disable csrf rules
                .and().csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }
}
