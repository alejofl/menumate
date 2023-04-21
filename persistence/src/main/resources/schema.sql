CREATE TABLE IF NOT EXISTS images
(
    image_id SERIAL PRIMARY KEY,
    bytes    BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   SERIAL PRIMARY KEY,
    email     VARCHAR(256) UNIQUE NOT NULL,
    password  VARCHAR(60),
    name      VARCHAR(48)         NOT NULL,
    image_id  INT                 REFERENCES images (image_id) ON DELETE SET NULL,
    is_active BOOLEAN             NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS restaurants
(
    restaurant_id SERIAL PRIMARY KEY,
    name          VARCHAR(32)  NOT NULL,
    email         VARCHAR(256) NOT NULL,
    logo_id       INT          REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_1_id INT          REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_2_id INT          REFERENCES images (image_id) ON DELETE SET NULL,
    address       TEXT,
    description   TEXT,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS roles
(
    role_level INT PRIMARY KEY,
    name       VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS restaurant_roles
(
    user_id       INT REFERENCES users (user_id) ON DELETE CASCADE             NOT NULL,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    role_level    INT REFERENCES roles (role_level) ON DELETE CASCADE          NOT NULL,

    PRIMARY KEY (user_id, restaurant_id)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id   SERIAL PRIMARY KEY,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    name          VARCHAR(32)                                                  NOT NULL,
    order_num     INT                                                          NOT NULL,

    UNIQUE (restaurant_id, order_num)
);

CREATE TABLE IF NOT EXISTS products
(
    product_id  SERIAL PRIMARY KEY,
    category_id INT REFERENCES categories (category_id) ON DELETE CASCADE NOT NULL,
    name        VARCHAR(128)                                              NOT NULL,
    price       DECIMAL(10, 2)                                            NOT NULL,
    description TEXT,
    image_id    INT                                                       REFERENCES images (image_id) ON DELETE SET NULL,
    available   BOOLEAN                                                   NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders
(
    order_id       SERIAL PRIMARY KEY,
    order_type     INT NOT NULL,
    restaurant_id  INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    user_id        INT REFERENCES users (user_id) ON DELETE CASCADE             NOT NULL,
    date_ordered   TIMESTAMP                                                    NOT NULL DEFAULT now(),
    date_delivered TIMESTAMP,
    address        TEXT,
    table_number   INT
);

CREATE TABLE IF NOT EXISTS order_items
(
    order_id    INT REFERENCES orders (order_id) ON DELETE CASCADE     NOT NULL,
    product_id  INT REFERENCES products (product_id) ON DELETE CASCADE NOT NULL,
    line_number INT                                                    NOT NULL,
    quantity    INT                                                    NOT NULL CHECK (quantity > 0),
    comment     TEXT,

    PRIMARY KEY (order_id, product_id, line_number)
);
