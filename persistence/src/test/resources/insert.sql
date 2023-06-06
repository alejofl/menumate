INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (420, 'user1@localhost', 'password', 'user', true, 'en');

INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (1337, 'user2@localhost', 'password', 'user', false, 'en');

INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (1500, 'user3@localhost', 'password', 'user', true, 'en');

INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (2000, 'user4@localhost', 'password', 'user', false, 'en');

INSERT INTO user_resetpassword_tokens (user_id, token, expires)
    VALUES (2000, '8ac27001-c568-4190-b6da-1a80478c', CURRENT_TIMESTAMP + INTERVAL '1' DAY);

INSERT INTO user_verification_tokens (user_id, token, expires)
    VALUES (2000, '8ac27001-d605-4190-abcd-1a80478c', CURRENT_TIMESTAMP + INTERVAL '1' DAY);

INSERT INTO user_addresses(user_id, address, name, last_used)
    VALUES (420, 'address1', 'home', now());

INSERT INTO user_addresses(user_id, address, name, last_used)
    VALUES (420, 'address2', 'work', CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO user_addresses(user_id, address, name, last_used)
    VALUES (420, 'address3', 'university', CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active)
    VALUES (506, 'C', 'restaurant@localhost', 10, 1, 1500, 'somewhere', 'description', now(), false, true);

INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active)
    VALUES (600, 'A', 'restaurant@localhost', 10, 2, 1500, 'somewhere', 'description', CURRENT_TIMESTAMP - INTERVAL '1' DAY, false, true);

INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active)
    VALUES (1200, 'B', 'restaurant@localhost', 10, 3, 1500, 'somewhere', 'description', CURRENT_TIMESTAMP - INTERVAL '2' DAY, false, true);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (506, 1);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (506, 2);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (506, 3);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (600, 1);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (600, 2);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (1200, 1);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (901, 506, 'category', 1, false);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (50, 506, 'category', 2, false);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (25, 506, 'category', 3, true);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (320, 600, 'category', 1, false);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (750, 600, 'category', 2, false);