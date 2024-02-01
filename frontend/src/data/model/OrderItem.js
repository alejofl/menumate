export default class OrderItem {
    constructor(comment, lineNumber, productUrl, quantity) {
        this.comment = comment;
        this.lineNumber = lineNumber;
        this.productUrl = productUrl;
        this.quantity = quantity;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new OrderItem(
            object.comment,
            object.lineNumber,
            object.productUrl,
            object.quantity
        );
    }
}
