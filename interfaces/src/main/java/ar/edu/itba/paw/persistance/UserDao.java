package ar.edu.itba.paw.persistance;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.UserAddress;

import java.util.Optional;

public interface UserDao {

    Optional<User> getById(long userId);

    Optional<User> getByEmail(String email);

    User create(String email, String password, String name, String language);

    void registerAddress(long userId, String address, String name);

    void deleteAddress(long userId, String address);

    void updateAddress(long userId, String oldAddress, String newAddress);

    void updateAddressName(long userId, String address, String name);

    Optional<UserAddress> getAddress(long userId, String address);
}
