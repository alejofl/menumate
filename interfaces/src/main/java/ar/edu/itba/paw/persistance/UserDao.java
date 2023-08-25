package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;

import java.util.Optional;

public interface UserDao {

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    Optional<UserAddress> getAddressById(long userId, long addressId);

    User create(String email, String password, String name, String language);

    UserAddress registerAddress(long userId, String address, String name);

    UserAddress updateAddress(long userId, long addressId, String address, String name);

    UserAddress refreshAddress(long userId, String address);

    void deleteAddress(long userId, long addressId);
}
