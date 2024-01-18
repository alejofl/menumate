export default class Review {
    constructor(comment, date, orderId, orderUrl, rating, reviewerName, selfUrl) {
        this.comment = comment;
        this.date = new Date(date);
        this.orderId = orderId;
        this.orderUrl = orderUrl;
        this.rating = rating;
        this.reviewerName = reviewerName;
        this.selfUrl = selfUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new Review(
            object.comment,
            object.date,
            object.orderId,
            object.orderUrl,
            object.rating,
            object.reviewerName,
            object.selfUrl
        );
    }
}
