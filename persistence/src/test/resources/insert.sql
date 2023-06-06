INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (420, 'user1@localhost', 'password', 'user', true, 'en');

INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (1337, 'user2@localhost', 'password', 'user', false, 'en');

INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (1500, 'user3@localhost', 'password', 'user', true, 'en');

INSERT INTO users (user_id, email, password, name, is_active, preferred_language)
    VALUES (2000, 'user4@localhost', 'password', 'user', false, 'en');

-- Tokens for user 2000
INSERT INTO user_resetpassword_tokens (user_id, token, expires)
    VALUES (2000, '8ac27001-c568-4190-b6da-1a80478c', CURRENT_TIMESTAMP + INTERVAL '1' DAY);

INSERT INTO user_verification_tokens (user_id, token, expires)
    VALUES (2000, '8ac27001-d605-4190-abcd-1a80478c', CURRENT_TIMESTAMP + INTERVAL '1' DAY);

-- Addresses for user 420
INSERT INTO user_addresses(user_id, address, name, last_used)
    VALUES (420, 'address1', 'home', now());

INSERT INTO user_addresses(user_id, address, name, last_used)
    VALUES (420, 'address2', 'work', CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO user_addresses(user_id, address, name, last_used)
    VALUES (420, 'address3', 'university', CURRENT_TIMESTAMP - INTERVAL '1' DAY);

-- Restaurants for user 1500
INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active)
    VALUES (506, 'C', 'restaurant@localhost', 10, 1, 1500, 'somewhere', 'description', now(), false, true);

INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active)
    VALUES (600, 'A', 'restaurant@localhost', 10, 2, 1500, 'somewhere', 'description', CURRENT_TIMESTAMP - INTERVAL '1' DAY, false, true);

INSERT INTO restaurants (restaurant_id, name, email, max_tables, specialty, owner_user_id, address, description, date_created, deleted, is_active)
    VALUES (1200, 'B', 'restaurant@localhost', 10, 3, 1500, 'somewhere', 'description', CURRENT_TIMESTAMP - INTERVAL '2' DAY, false, true);

-- Tag for restaurant 506
INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (506, 1);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (506, 2);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (506, 3);

-- Tags for restaurant 600
INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (600, 1);

INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (600, 2);

-- Tag for restaurant 1200
INSERT INTO restaurant_tags (restaurant_id, tag_id)
    VALUES (1200, 1);

-- Categories for restaurant 506
INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (901, 506, 'category', 1, false);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (50, 506, 'category', 2, false);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (25, 506, 'category', 3, true);

--Categories for restaurant 600
INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (320, 600, 'category', 1, false);

INSERT INTO categories(category_id, restaurant_id, name, order_num, deleted)
    VALUES (750, 600, 'category', 2, false);

-- Order and reviews for restaurant 506
INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (1700, 506, 1, now(), 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (1701, 506, 1, CURRENT_TIMESTAMP - INTERVAL '1' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (1702, 506, 1, CURRENT_TIMESTAMP - INTERVAL '2' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (1703, 506, 1, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (1704, 506, 1, CURRENT_TIMESTAMP - INTERVAL '4' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (1705, 506, 1, CURRENT_TIMESTAMP - INTERVAL '5' DAY, 420);

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (1700, 1, CURRENT_TIMESTAMP + INTERVAL '1' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (1701, 1, CURRENT_TIMESTAMP + INTERVAL '2' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (1702, 3, CURRENT_TIMESTAMP + INTERVAL '3' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (1703, 5, CURRENT_TIMESTAMP + INTERVAL '4' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (1704, 5, CURRENT_TIMESTAMP + INTERVAL '5' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (1705, 5, CURRENT_TIMESTAMP + INTERVAL '6' DAY, 'comment', 'reply');

-- Order and reviews for restaurant 600
INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (2000, 600, 1, now(), 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (2001, 600, 1, CURRENT_TIMESTAMP - INTERVAL '1' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (2002, 600, 1, CURRENT_TIMESTAMP - INTERVAL '2' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (2003, 600, 1, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (2004, 600, 1, CURRENT_TIMESTAMP - INTERVAL '4' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (2005, 600, 1, CURRENT_TIMESTAMP - INTERVAL '5' DAY, 420);

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (2000, 2, CURRENT_TIMESTAMP + INTERVAL '1' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (2001, 2, CURRENT_TIMESTAMP + INTERVAL '2' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (2002, 2, CURRENT_TIMESTAMP + INTERVAL '3' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (2003, 2, CURRENT_TIMESTAMP + INTERVAL '4' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (2004, 2, CURRENT_TIMESTAMP + INTERVAL '5' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (2005, 2, CURRENT_TIMESTAMP + INTERVAL '6' DAY, 'comment', 'reply');

-- Order and reviews for restaurant 1200
INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (3000, 1200, 1, now(), 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (3001, 1200, 1, CURRENT_TIMESTAMP - INTERVAL '1' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (3002, 1200, 1, CURRENT_TIMESTAMP - INTERVAL '2' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (3003, 1200, 1, CURRENT_TIMESTAMP - INTERVAL '3' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (3004, 1200, 1, CURRENT_TIMESTAMP - INTERVAL '4' DAY, 420);

INSERT INTO orders (order_id, restaurant_id, order_type, date_ordered, user_id)
    VALUES (3005, 1200, 1, CURRENT_TIMESTAMP - INTERVAL '5' DAY, 420);

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (3000, 3, CURRENT_TIMESTAMP + INTERVAL '1' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (3001, 3, CURRENT_TIMESTAMP + INTERVAL '2' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (3002, 3, CURRENT_TIMESTAMP + INTERVAL '3' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (3003, 3, CURRENT_TIMESTAMP + INTERVAL '4' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (3004, 3, CURRENT_TIMESTAMP + INTERVAL '5' DAY, 'comment', 'reply');

INSERT INTO order_reviews (order_id, rating, date, comment, reply)
    VALUES (3005, 3, CURRENT_TIMESTAMP + INTERVAL '6' DAY, 'comment', 'reply');


-- Products for restaurant 506 and category 901
INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (100, 'product', 100, 901, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (101, 'product', 200, 901, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (102, 'product', 300, 901, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (103, 'product', 400, 901, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (104, 'product', 500, 901, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (105, 'product', 600, 901, true, 'description', false);

-- Products for restaurant 600 and category 320
INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (200, 'product', 700, 320, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (201, 'product', 800, 320, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (202, 'product', 900, 320, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (203, 'product', 1000, 320, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (204, 'product', 1100, 320, true, 'description', false);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (205, 'product', 1200, 320, true, 'description', false);


-- Adding some deleted products for 320 and 901
INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (300, 'product', 535.55, 901, true, 'description', true);

INSERT INTO products (product_id, name, price, category_id, available, description, deleted)
    VALUES (400, 'product', 535.55, 320, true, 'description', true);


-- Adding order items for order 506
INSERT INTO order_items (order_id, line_number, product_id, quantity, comment)
    VALUES (1700, 1, 100, 10, 'comment');

INSERT INTO order_items (order_id, line_number, product_id, quantity, comment)
    VALUES (1701, 2, 101, 10, 'comment');

INSERT INTO order_items (order_id, line_number, product_id, quantity, comment)
    VALUES (1702, 3, 102, 10, 'comment');