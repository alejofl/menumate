package ar.edu.itba.paw.persistence;

class TableFields {

    static final String USERS_FIELDS = "users.user_id user_id, users.email user_email, users.name user_name, users.image_id user_image_id, users.is_active user_is_active";

    static final String RESTAURANTS_FIELDS = "restaurants.restaurant_id restaurant_id, restaurants.name restaurant_name, restaurants.email restaurant_email, restaurants.specialty restaurant_specialty, restaurants.owner_user_id restaurant_owner_user_id, restaurants.logo_id restaurant_logo_id, restaurants.portrait_1_id restaurant_portrait_1_id, restaurants.portrait_2_id restaurant_portrait_2_id, restaurants.address restaurant_address, restaurants.description restaurant_description, restaurants.max_tables restaurant_max_tables, restaurants.is_active restaurant_is_active, restaurants.deleted restaurant_deleted";

    static final String RESTAURANT_TAGS_FIELDS = "restaurant_tags.restaurant_id restaurant_id, restaurant_tags.tag_id tag_id";
    
    static final String CATEGORIES_FIELDS = "categories.category_id category_id, categories.name category_name, categories.order_num category_order, categories.deleted category_deleted";

    static final String ORDERS_FIELDS = "orders.order_id order_id, orders.order_type order_type, orders.date_ordered order_date_ordered, orders.date_confirmed order_date_confirmed, orders.date_ready order_date_ready, orders.date_delivered order_date_delivered, orders.date_cancelled order_date_cancelled, orders.table_number order_table_number, orders.address order_address";

    static final String PRODUCTS_FIELDS = "products.product_id product_id, products.name product_name, products.price product_price, products.description product_description, products.image_id product_image_id, products.available product_available, products.deleted product_deleted";

    static final String ORDER_ITEMS_FIELDS = "order_items.line_number order_item_line_number, order_items.quantity order_item_quantity, order_items.comment order_item_comment";

    static final String ORDER_REVIEW_FIELDS = "order_reviews.rating order_review_rating, order_reviews.date order_review_date, order_reviews.comment order_review_comment";
}
