CREATE TABLE IF NOT EXISTS images
(
    image_id SERIAL PRIMARY KEY,
    bytes    BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id            SERIAL PRIMARY KEY,
    email              VARCHAR(320) UNIQUE NOT NULL,
    password           VARCHAR(60),
    name               VARCHAR(50) NOT NULL,
    date_joined        TIMESTAMP NOT NULL DEFAULT now(),
    image_id           INT REFERENCES images (image_id) ON DELETE SET NULL,
    is_active          BOOLEAN NOT NULL DEFAULT FALSE,
    preferred_language VARCHAR(3) NOT NULL DEFAULT 'en'
);

CREATE TABLE IF NOT EXISTS user_verification_tokens
(
    user_id INT PRIMARY KEY REFERENCES users (user_id) ON DELETE CASCADE,
    token   VARCHAR(32) UNIQUE NOT NULL,
    expires TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS user_resetpassword_tokens
(
    user_id INT PRIMARY KEY REFERENCES users (user_id) ON DELETE CASCADE,
    token   VARCHAR(32) UNIQUE NOT NULL,
    expires TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS restaurants
(
    restaurant_id SERIAL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    email         VARCHAR(320) NOT NULL,
    specialty     SMALLINT NOT NULL,
    owner_user_id INT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    date_created  TIMESTAMP NOT NULL DEFAULT now(),
    address       VARCHAR(200),
    description   VARCHAR(300),
    max_tables    INT NOT NULL CHECK (max_tables > 0),
    logo_id       INT REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_1_id INT REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_2_id INT REFERENCES images (image_id) ON DELETE SET NULL,
    is_active     BOOLEAN NOT NULL DEFAULT TRUE,
    deleted       BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS restaurant_roles
(
    user_id       INT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    role_level    SMALLINT NOT NULL CHECK (role_level > 0),

    PRIMARY KEY (user_id, restaurant_id)
);

CREATE TABLE IF NOT EXISTS restaurant_tags
(
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    tag_id        SMALLINT NOT NULL,
    PRIMARY KEY (restaurant_id, tag_id)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id   SERIAL PRIMARY KEY,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    name          VARCHAR(50) NOT NULL,
    order_num     SMALLINT NOT NULL,
    deleted       BOOLEAN NOT NULL DEFAULT FALSE,

    UNIQUE (restaurant_id, order_num)
);

CREATE TABLE IF NOT EXISTS products
(
    product_id  SERIAL PRIMARY KEY,
    category_id INT REFERENCES categories (category_id) ON DELETE CASCADE NOT NULL,
    name        VARCHAR(150) NOT NULL,
    description VARCHAR(300),
    image_id    INT REFERENCES images (image_id) ON DELETE SET NULL,
    price       DECIMAL(10, 2) NOT NULL CHECK (price > 0),
    available   BOOLEAN NOT NULL DEFAULT TRUE,
    deleted     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS orders
(
    order_id       SERIAL PRIMARY KEY,
    order_type     SMALLINT NOT NULL,
    restaurant_id  INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    user_id        INT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    date_ordered   TIMESTAMP NOT NULL DEFAULT now(),
    date_confirmed TIMESTAMP DEFAULT NULL,
    date_ready     TIMESTAMP DEFAULT NULL,
    date_delivered TIMESTAMP DEFAULT NULL,
    date_cancelled TIMESTAMP DEFAULT NULL,
    address        VARCHAR(300),
    table_number   SMALLINT
);

CREATE TABLE IF NOT EXISTS order_items
(
    order_id    INT REFERENCES orders (order_id) ON DELETE CASCADE NOT NULL,
    product_id  INT REFERENCES products (product_id) ON DELETE CASCADE NOT NULL,
    line_number SMALLINT NOT NULL CHECK (line_number > 0),
    quantity    SMALLINT NOT NULL CHECK (quantity > 0),
    comment     VARCHAR(120),

    PRIMARY KEY (order_id, product_id, line_number)
);

CREATE TABLE IF NOT EXISTS order_reviews
(
    order_id INT REFERENCES orders (order_id) ON DELETE CASCADE PRIMARY KEY,
    rating   SMALLINT NOT NULL CHECK (rating >= 0 AND rating <= 5),
    date     TIMESTAMP NOT NULL DEFAULT now(),
    comment  VARCHAR(500)
);


DROP VIEW IF EXISTS restaurant_details;
CREATE VIEW restaurant_details AS
(
    SELECT restaurants.*,
    COALESCE(AVG(CAST(order_reviews.rating AS FLOAT)), 0) AS average_rating,
    COUNT(order_reviews.order_id) AS review_count,
    (
        SELECT COALESCE(AVG(products.price), 0) FROM products
            JOIN categories ON categories.category_id = products.category_id
            WHERE categories.restaurant_id = restaurants.restaurant_id
                AND products.deleted = false AND products.available = true
    ) AS average_price
    FROM restaurants LEFT OUTER JOIN (orders JOIN order_reviews ON orders.order_id = order_reviews.order_id)
        ON restaurants.restaurant_id = orders.restaurant_id
    WHERE restaurants.deleted = false AND restaurants.is_active = true
    GROUP BY restaurants.restaurant_id
);
