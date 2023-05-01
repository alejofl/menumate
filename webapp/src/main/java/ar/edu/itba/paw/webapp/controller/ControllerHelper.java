package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class ControllerHelper {

    @Autowired
    private UserService userService;

    @ModelAttribute("currentUser")
    public User addCurrentUser() {
        String email = ControllerUtils.getCurrentUserEmail();
        Optional<User> user;
        if (email == null || !(user = userService.getByEmail(email)).isPresent())
            return null;

        return user.get();
    }
}
