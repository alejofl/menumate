CREATE TABLE IF NOT EXISTS images
(
    image_id SERIAL PRIMARY KEY,
    bytes    BYTEA NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id   SERIAL PRIMARY KEY,
    username  VARCHAR(32) UNIQUE,
    password  VARCHAR(128),
    email     VARCHAR(256) UNIQUE NOT NULL,
    image_id  INT                 REFERENCES images (image_id) ON DELETE SET NULL,
    is_active BOOLEAN DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS restaurants
(
    restaurant_id SERIAL PRIMARY KEY,
    name          VARCHAR(32) NOT NULL,
    logo_id       INT         REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_1_id INT         REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_2_id INT         REFERENCES images (image_id) ON DELETE SET NULL,
    address       TEXT,
    description   TEXT,
    is_active     BOOLEAN DEFAULT TRUE
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
    name        VARCHAR(32)                                               NOT NULL,
    price       DECIMAL(10, 2)                                            NOT NULL,
    description TEXT,
    image_id    INT                                                       REFERENCES images (image_id) ON DELETE SET NULL,
    available   BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS order_types
(
    order_type_id SERIAL PRIMARY KEY,
    name          VARCHAR(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders
(
    order_id      SERIAL PRIMARY KEY,
    order_type_id INT REFERENCES order_types (order_type_id) ON DELETE CASCADE NOT NULL,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    user_id       INT REFERENCES users (user_id) ON DELETE CASCADE             NOT NULL,
    order_date    TIMESTAMP DEFAULT now(),
    table_number  INT,
    address       TEXT
);

CREATE TABLE IF NOT EXISTS products_x_order
(
    order_id   INT REFERENCES orders (order_id) ON DELETE CASCADE     NOT NULL,
    product_id INT REFERENCES products (product_id) ON DELETE CASCADE NOT NULL,

    PRIMARY KEY (order_id, product_id)
);