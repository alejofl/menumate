package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class PawUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final Pair<User, String> userAndPassword = us.getByEmailWithPassword(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user for email " + email));

        return new PawAuthUserDetails(userAndPassword.getKey().getEmail(), userAndPassword.getValue(), new ArrayList<>());
    }
}
