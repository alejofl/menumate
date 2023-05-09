package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.VerificationService;
import ar.edu.itba.paw.webapp.exception.UserNotVerifiedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
public class CustomAuthenticationEntryPoint extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private EmailService emailService;

    private static final String LOGIN_URL = "/auth/login";
    private static final String VERIFY_EMAIL_ERROR = "?verify=emailed";
    private static final String NOT_VERIFIED_ERROR = "?error=not_verified";
    private static final String INVALID_CREDENTIALS_ERROR = "?error=invalid_credentials";
    private static final String MAILER_ERROR = "?error=mailer_error";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception.getCause() instanceof UserNotVerifiedException) {
            UserNotVerifiedException e = (UserNotVerifiedException) exception.getCause();
            if (!verificationService.hasActiveVerificationToken(e.getEmail())) {
                String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), request.getContextPath());
                String token = verificationService.generateVerificationToken(e.getEmail());
                try {
                    emailService.sendUserVerificationEmail(baseUrl, e.getEmail(), token);
                    setDefaultFailureUrl(LOGIN_URL + VERIFY_EMAIL_ERROR);
                } catch (MessagingException ex) {
                    setDefaultFailureUrl(LOGIN_URL + MAILER_ERROR);
                }
            } else {
                setDefaultFailureUrl(LOGIN_URL + NOT_VERIFIED_ERROR);
            }
        } else {
            setDefaultFailureUrl(LOGIN_URL + INVALID_CREDENTIALS_ERROR);
        }

        super.onAuthenticationFailure(request, response, exception);
    }
}
