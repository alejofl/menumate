package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exception.UserNotFoundException;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.ResetPasswordTokenDao;
import ar.edu.itba.paw.persistance.UserDao;
import ar.edu.itba.paw.persistance.VerificationTokenDao;
import ar.edu.itba.paw.service.EmailService;
import ar.edu.itba.paw.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.Optional;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @Autowired
    private ResetPasswordTokenDao resetPasswordTokenDao;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserDetailsService userDetailsService;

    @Transactional
    @Override
    public void sendUserVerificationToken(final User user) throws MessagingException {
        if (!user.getIsActive()) {
            String token = verificationTokenDao.generateToken(user.getUserId());
            emailService.sendUserVerificationEmail(user, token);
        }
    }

    @Transactional
    @Override
    public void sendPasswordResetToken(User user) throws MessagingException {
        if (user.getIsActive()) {
            String token = resetPasswordTokenDao.generateToken(user.getUserId());
            emailService.sendResetPasswordEmail(user, token);
        }
    }

    @Transactional
    @Override
    public boolean verifyUserAndDeleteVerificationToken(final String token) {
        Optional<Long> userId = verificationTokenDao.deleteTokenAndRetrieveUserId(token);
        if (!userId.isPresent())
            return false;

        userDao.updateUserActive(userId.get(), true);

        String email = userDao.getById(userId.get()).orElseThrow(UserNotFoundException::new).getEmail();
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetailsService.loadUserByUsername(email), null, AuthorityUtils.NO_AUTHORITIES);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return true;
    }

    @Transactional
    @Override
    public boolean updatePasswordAndDeleteResetPasswordToken(String token, String newPassword) {
        Optional<Long> userId = resetPasswordTokenDao.deleteTokenAndRetrieveUserId(token);
        if (!userId.isPresent() || newPassword == null)
            return false;

        newPassword = passwordEncoder.encode(newPassword);
        userDao.updatePassword(userId.get(), newPassword);
        return true;
    }

    @Override
    public boolean hasActiveVerificationToken(final long userId) {
        return verificationTokenDao.hasActiveToken(userId);
    }

    @Override
    public boolean isValidResetPasswordToken(String token) {
        return resetPasswordTokenDao.isValidToken(token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteStaledVerificationTokens() {
        verificationTokenDao.deleteStaledTokens();
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deletedStaledResetPasswordTokens() {
        resetPasswordTokenDao.deleteStaledTokens();
    }
}
