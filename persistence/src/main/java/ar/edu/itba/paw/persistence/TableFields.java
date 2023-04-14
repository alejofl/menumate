package ar.edu.itba.paw.persistence;

class TableFields {

    static final String USERS_FIELDS = "users.user_id user_id, users.username user_username, users.password user_password, users.email user_email, users.image_id user_image_id, users.is_active user_is_active";

    static final String RESTAURANTS_FIELDS = "restaurants.restaurant_id restaurant_id, restaurants.name restaurant_name, restaurants.logo_id restaurant_logo_id, restaurants.portrait_1_id restaurant_portrait_1_id, restaurants.portrait_2_id restaurant_portrait_2_id, restaurants.address restaurant_address, restaurants.description restaurant_description, restaurants.is_active restaurant_is_active";

    static final String CATEGORIES_FIELDS = "categories.category_id category_id, categories.name category_name, categories.order_num category_order";

    static final String ORDER_TYPES_FIELDS = "order_types.order_type_id order_type_id, order_types.name order_type_name";

    static final String ORDERS_FIELDS = "orders.order_id order_id, orders.order_date order_date, orders.table_number order_table_number, orders.address order_address";

    static final String PRODUCTS_FIELDS = "products.product_id product_id, products.name product_name, products.price product_price, products.description product_description, products.image_id product_image_id, products.available product_available";

}
