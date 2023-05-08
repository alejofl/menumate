package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.service.VerificationService;
import ar.edu.itba.paw.webapp.exception.UserNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private VerificationService verificationService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception.getCause() instanceof UserNotVerifiedException) {
            setDefaultFailureUrl("/auth/login?error=not_verified");
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
