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
                .antMatchers("/restaurants/{restaurant_id:\\d+}/edit/**").access("@accessValidator.checkRestaurantAdmin(#restaurant_id)")
                .antMatchers(HttpMethod.GET, "/restaurants/{restaurant_id:\\d+}/orders/**").access("@accessValidator.checkRestaurantOrderHandler(#restaurant_id)")
                .antMatchers(HttpMethod.POST, "/restaurants/{restaurant_id:\\d+}/orders").permitAll()
                .antMatchers(HttpMethod.GET, "/user/orders/**").authenticated()
                .antMatchers(HttpMethod.GET, "/orders/{order_id:\\d+}/**").access("@accessValidator.checkOrderOwnerOrHandler(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/pending").access("@accessValidator.checkOrderHandler(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/confirmed").access("@accessValidator.checkOrderHandler(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/ready").access("@accessValidator.checkOrderHandler(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/delivered").access("@accessValidator.checkOrderHandler(#order_id)")
                .antMatchers(HttpMethod.POST, "/orders/{order_id:\\d+}/cancel").access("@accessValidator.checkOrderHandler(#order_id)")
                .antMatchers("/restaurants/create").authenticated()
                .antMatchers("/**").permitAll()
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
