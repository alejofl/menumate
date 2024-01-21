
export default class Order {
    constructor(dateConfirmed, dateDelivered, dataOrdered, dateReady, itemsUrl, orderId, orderType, restaurantUrl, selfUrl, status, userUrl) {
        this.dateConfirmed = new Date(dateConfirmed);
        this.dateDelivered = new Date(dateDelivered);
        this.dataOrdered = new Date(dataOrdered);
        this.dateReady = new Date(dateReady);
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
            object.dateConfirmed,
            object.dateDelivered,
            object.dataOrdered,
            object.dateReady,
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
