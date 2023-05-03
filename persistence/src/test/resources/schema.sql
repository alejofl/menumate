CREATE TABLE IF NOT EXISTS images
(
    image_id SERIAL PRIMARY KEY,
    bytes    BLOB NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id     SERIAL PRIMARY KEY,
    email       VARCHAR(320) UNIQUE NOT NULL,
    password    VARCHAR(60),
    name        VARCHAR(50) NOT NULL,
    date_joined TIMESTAMP NOT NULL DEFAULT now(),
    image_id    INT REFERENCES images (image_id) ON DELETE SET NULL,
    is_active   BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS restaurants
(
    restaurant_id SERIAL PRIMARY KEY,
    name          VARCHAR(50) NOT NULL,
    email         VARCHAR(320) NOT NULL,
    owner_user_id INT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    date_created  TIMESTAMP NOT NULL DEFAULT now(),
    logo_id       INT REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_1_id INT REFERENCES images (image_id) ON DELETE SET NULL,
    portrait_2_id INT REFERENCES images (image_id) ON DELETE SET NULL,
    address       VARCHAR(200),
    description   VARCHAR(300),
    is_active     BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS restaurant_roles
(
    user_id       INT REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    role_level    SMALLINT NOT NULL,

    PRIMARY KEY (user_id, restaurant_id)
);

CREATE TABLE IF NOT EXISTS categories
(
    category_id   SERIAL PRIMARY KEY,
    restaurant_id INT REFERENCES restaurants (restaurant_id) ON DELETE CASCADE NOT NULL,
    name          VARCHAR(50) NOT NULL,
    order_num     SMALLINT NOT NULL,

    UNIQUE (restaurant_id, order_num)
);

CREATE TABLE IF NOT EXISTS products
(
    product_id  SERIAL PRIMARY KEY,
    category_id INT REFERENCES categories (category_id) ON DELETE CASCADE NOT NULL,
    name        VARCHAR(150) NOT NULL,
    price       DECIMAL(10, 2) NOT NULL,
    description VARCHAR(300),
    image_id    INT REFERENCES images (image_id) ON DELETE SET NULL,
    available   BOOLEAN NOT NULL DEFAULT TRUE
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
    date_canceled  TIMESTAMP DEFAULT NULL,
    address        VARCHAR(300),
    table_number   SMALLINT
);

CREATE TABLE IF NOT EXISTS order_items
(
    order_id    INT REFERENCES orders (order_id) ON DELETE CASCADE NOT NULL,
    product_id  INT REFERENCES products (product_id) ON DELETE CASCADE NOT NULL,
    line_number SMALLINT NOT NULL,
    quantity    SMALLINT NOT NULL CHECK (quantity > 0),
    comment     VARCHAR(120),

    PRIMARY KEY (order_id, product_id, line_number)
);
