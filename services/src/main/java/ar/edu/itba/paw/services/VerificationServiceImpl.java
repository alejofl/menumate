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
    public String generateVerificationToken(String email) {
        return verificationDao.generateVerificationToken(email);
    }

    @Override
    public boolean verificationTokenIsValid(String email, String token) {
        return verificationDao.verificationTokenIsValid(email, token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteStaledVerificationTokens() {
        verificationDao.deleteStaledVerificationTokens();
    }

    @Override
    public boolean verificationTokenIsStaled(String email) {
        return verificationDao.verificationTokenIsStaled(email);
    }

    @Override
    public boolean deleteVerificationToken(String email) {
        return verificationDao.deleteVerificationToken(email);
    }
}
