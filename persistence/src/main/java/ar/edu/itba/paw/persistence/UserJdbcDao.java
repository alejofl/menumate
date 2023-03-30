package ar.edu.itba.paw.persistence;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.persistance.UserDao;
import org.springframework.stereotype.Repository;

@Repository
public class UserJdbcDao implements UserDao {
    @Override
    public User getUserById(long id) {
        return new User(id, "Paw from the db", "password");
    }
}
