package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.util.Pair;
import ar.edu.itba.paw.webapp.exception.UserNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PawUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, UserNotVerifiedException {
        final User user = userService.getByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user for email " + email));

        if (!user.getIsActive())
            throw new UserNotVerifiedException("User exists but is not verified", user);

        if (user.getPassword() == null)
            throw new UsernameNotFoundException("User exists but is not consolidated");

        return new PawAuthUserDetails(user.getUserId(), user.getEmail(), user.getPassword(), new ArrayList<>());
    }
}
