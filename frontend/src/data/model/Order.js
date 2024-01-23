export default class Order {
    constructor(
        orderId,
        orderType,
        dateOrdered,
        dateConfirmed,
        dateReady,
        dateDelivered,
        dateCancelled,
        status,
        address,
        tableNumber,
        selfUrl,
        itemsUrl,
        userUrl,
        restaurantUrl
    ) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.dateOrdered = new Date(dateOrdered);
        this.dateConfirmed = new Date(dateConfirmed);
        this.dateReady = new Date(dateReady);
        this.dateDelivered = new Date(dateDelivered);
        this.dateCancelled = new Date(dateCancelled);
        this.status = status;
        this.address = address;
        this.tableNumber = tableNumber;
        this.selfUrl = selfUrl;
        this.itemsUrl = itemsUrl;
        this.userUrl = userUrl;
        this.restaurantUrl = restaurantUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new Order(
            object.orderId,
            object.orderType,
            object.dateOrdered,
            object.dateConfirmed,
            object.dateReady,
            object.dateDelivered,
            object.dateCancelled,
            object.status,
            object.address,
            object.tableNumber,
            object.selfUrl,
            object.itemsUrl,
            object.userUrl,
            object.restaurantUrl
        );
    }
}
