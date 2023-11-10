package ar.edu.itba.paw.persistence.constants;

import ar.edu.itba.paw.model.RestaurantRoleLevel;
import ar.edu.itba.paw.model.UserRoleLevel;

import java.time.LocalDateTime;

public final class UserConstants {

    public static final long ACTIVE_USER_ID = 420;
    public static final long INACTIVE_USER_ID = 1337;
    public static final long RESTAURANT_OWNER_ID = 1500;
    public static final long USER_ID_WITH_TOKENS = 2000;
    public static final long USER_ID_RESTAURANT_ADMIN_ROLE = 3000;
    public static final long USER_ID_MODERATOR_ROLE = 3500;
    public static final String ACTIVE_USER_EMAIL = "user1@localhost";
    public static final String INACTIVE_USER_EMAIL = "user2@localhost";
    public static final String RESTAURANT_OWNER_EMAIL = "user3@localhost";
    public static final String USER_EMAIL_WITH_TOKENS = "user4@localhost";
    public static final String USER_MODERATOR_EMAIL = "moderator@localhost";
    public static final String PASSWORD = "password";
    public static final String USERNAME = "user";
    public static final String PREFERRED_LANGUAGE = "en";
    public static final int ADDRESSES_COUNT = 3;
    public static final String LAST_USED_ADDRESS = "address1";
    public static final long LAST_USED_ADDRESS_ID = 99990;
    public static final String LAST_USED_ADDRESS_NAME = "home";
    public static final String PREVIOUS_USED_ADDRESS = "address2";
    public static final String TOKEN1 = "8ac27000-c568-4070-b6da-1a80478c";
    public static final String TOKEN2 = "3ab27010-c538-4180-a6pa-1b80581c";
    public static final String UNUSED_TOKEN = "5CA27010-c538-4180-a6pa-1b80581c";
    public static final String ACTIVE_RESET_PASSWORD_TOKEN = "8ac27001-c568-4190-b6da-1a80478c";
    public static final String ACTIVE_VERIFICATION_TOKEN = "8ac27001-d605-4190-abcd-1a80478c";
    public static final LocalDateTime TOKEN_EXPIRATION = LocalDateTime.now().plusDays(1);
    public static final RestaurantRoleLevel ADMIN_ROLE = RestaurantRoleLevel.ADMIN;
    public static final UserRoleLevel MODERATOR_ROLE = UserRoleLevel.MODERATOR;
    public static final RestaurantRoleLevel ORDER_HANDLER_ROLE = RestaurantRoleLevel.ORDER_HANDLER;

    private UserConstants() {
    }
}
