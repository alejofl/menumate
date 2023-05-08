package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.util.Pair;
import ar.edu.itba.paw.service.UserService;
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
    private UserService us;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, UserNotVerifiedException {
        final Pair<User, String> userAndPassword = us.getByEmailWithPassword(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user for email " + email));


        int userId = userAndPassword.getKey().getUserId();
        String userEmail = userAndPassword.getKey().getEmail();
        String userPassword = userAndPassword.getValue();
        boolean isActive = userAndPassword.getKey().getIsActive();

        if (!isActive)
            throw new UserNotVerifiedException("User exists but is not verified", email);

        if (userPassword == null)
            throw new UsernameNotFoundException("User exists but is not consolidated");

        return new PawAuthUserDetails(userId, userEmail, userPassword, new ArrayList<>());
    }
}
