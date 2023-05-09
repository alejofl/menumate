package ar.edu.itba.paw.services;

import ar.edu.itba.paw.persistance.VerificationDao;
import ar.edu.itba.paw.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private VerificationDao verificationDao;

    @Override
    public String generateVerificationToken(final String email) {
        return verificationDao.generateVerificationToken(email);
    }

    @Override
    public boolean verifyAndDeleteToken(final String token) {
        return verificationDao.verifyAndDeleteToken(token);
    }

    @Override
    public boolean hasActiveVerificationToken(final String email) {
        return verificationDao.hasActiveVerificationToken(email);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteStaledVerificationTokens() {
        verificationDao.deleteStaledVerificationTokens();
    }
}
