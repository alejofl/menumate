export default class Category {
    constructor(deleted, name, orderNum, productsUrl, restaurantUrl, selfUrl) {
        this.deleted = deleted;
        this.name = name;
        this.orderNum = orderNum;
        this.productsUrl = productsUrl;
        this.restaurantUrl = restaurantUrl;
        this.selfUrl = selfUrl;
    }

    static fromJSON(object) {
        return new Category(
            object.deleted,
            object.name,
            object.orderNum,
            object.productsUrl,
            object.restaurantUrl,
            object.selfUrl
        );
    }
}
