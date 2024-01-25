package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserRoleLevel;
import ar.edu.itba.paw.service.UserRoleService;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.exception.UserNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Component
public class PawUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException, UserNotVerifiedException {
        final User user = userService.getByEmail(email).orElseThrow(() -> new UsernameNotFoundException("No user for email " + email));

        if (!user.getIsActive())
            throw new UserNotVerifiedException(user);

        if (user.getPassword() == null)
            throw new UsernameNotFoundException("User exists but is not consolidated");

        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        final Optional<UserRoleLevel> userRole = userRoleService.getRole(user.getUserId());

        if (userRole.isPresent()) {
            authorities.add(new SimpleGrantedAuthority(userRole.get().getMessageCode()));
        }
        return new PawAuthUserDetails(user.getUserId(), user.getEmail(), user.getPassword(), authorities);
    }
}
