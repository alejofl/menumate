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
    public String generateVerificationToken(final int userId) {
        return verificationTokenDao.generateToken(userId);
    }

    @Override
    public String generatePasswordResetToken(int userId) {
        return resetPasswordTokenDao.generateToken(userId);
    }

    @Override
    public boolean verifyAndDeleteVerificationToken(final String token) {
        return verificationTokenDao.verifyAndDeleteToken(token);
    }

    @Override
    public boolean verifyAndDeletePasswordResetToken(String token) {
        return resetPasswordTokenDao.verifyAndDeleteToken(token);
    }

    @Override
    public boolean hasActiveVerificationToken(final int userId) {
        return verificationTokenDao.hasActiveToken(userId);
    }

    @Override
    public boolean hasActiveResetPasswordToken(final int userId) {
        return resetPasswordTokenDao.hasActiveToken(userId);
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
