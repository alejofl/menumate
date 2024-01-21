
export default class Order {
    constructor(dataOrdered, itemsUrl, orderId, orderType, restaurantUrl, selfUrl, status, userUrl) {
        this.dataOrdered = new Date(dataOrdered);
        this.itemsUrl = itemsUrl;
        this.orderId = orderId;
        this.orderType = orderType,
        this.restaurantUrl = restaurantUrl;
        this.selfUrl = selfUrl;
        this.status = status;
        this.userUrl = userUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new Order(
            object.dataOrdered,
            object.itemsUrl,
            object.orderId,
            object.orderType,
            object.restaurantUrl,
            object.selfUrl,
            object.status,
            object.userUrl
        );
    }
}
