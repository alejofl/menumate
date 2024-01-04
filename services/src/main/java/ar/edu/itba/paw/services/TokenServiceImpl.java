package ar.edu.itba.paw.services;

import ar.edu.itba.paw.model.Token;
import ar.edu.itba.paw.model.TokenType;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.TokenDao;
import ar.edu.itba.paw.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private TokenDao tokenDao;
    private static final Integer TOKEN_DURATION_DAYS = 1;
    private final static Logger LOGGER = LoggerFactory.getLogger(TokenServiceImpl.class);

    private static LocalDateTime generateTokenExpirationDate() {
        return LocalDateTime.now().plusDays(TOKEN_DURATION_DAYS);
    }

    private static String generateToken() {
        return UUID.randomUUID().toString().substring(0, 32);
    }

    @Override
    public Optional<Token> getByToken(String token) {
        return tokenDao.getByToken(token);
    }

    @Transactional
    @Override
    public Token createOrRefresh(User user, TokenType type) {
        Optional<Token> maybeToken = tokenDao.getByUserId(user.getUserId(), type);
        Token token;
        if (maybeToken.isPresent()) {
            token = maybeToken.get();
            if (!token.isFresh()) {
                token = tokenDao.refresh(token, generateToken(), generateTokenExpirationDate());
            }
        } else {
            token = tokenDao.create(user, type, generateToken(), generateTokenExpirationDate());
        }
        return token;
    }

    @Transactional
    @Override
    public void delete(Token token) {
        tokenDao.delete(token);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void deletedStaledTokens() {
        tokenDao.deleteStaledTokens();
    }
}
