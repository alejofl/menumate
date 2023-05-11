package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.ResetPasswordTokenDao;
import ar.edu.itba.paw.persistance.VerificationTokenDao;
import ar.edu.itba.paw.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private VerificationTokenDao verificationTokenDao;

    @Autowired
    private ResetPasswordTokenDao resetPasswordTokenDao;

    @Override
    public String generateVerificationToken(final long userId) {
        return verificationTokenDao.generateToken(userId);
    }

    @Override
    public String generatePasswordResetToken(long userId) {
        return resetPasswordTokenDao.generateToken(userId);
    }

    @Override
    public boolean verifyUserAndDeleteVerificationToken(final String token) {
        return verificationTokenDao.verifyUserAndDeleteToken(token);
    }

    @Override
    public boolean updatePasswordAndDeleteResetPasswordToken(String token, String newPassword) {
        return resetPasswordTokenDao.updatePasswordAndDeleteToken(token, newPassword);
    }

    @Override
    public boolean hasActiveVerificationToken(final long userId) {
        return verificationTokenDao.hasActiveToken(userId);
    }

    @Override
    public boolean hasActiveResetPasswordToken(final long userId) {
        return resetPasswordTokenDao.hasActiveToken(userId);
    }

    @Override
    public boolean isValidVerificationToken(String token) {
        return verificationTokenDao.isValidToken(token);
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
    public void deletedStaledResetPasswordTokens(){
        resetPasswordTokenDao.deleteStaledTokens();
    }
}
