package ar.edu.itba.paw.webapp.auth;

import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AccessValidator {

    public boolean checkEditRestaurant(HttpServletRequest request, int id) {
        // TODO: Implement. This is just a placeholder.
        return id % 2 == 0;
    }
}
