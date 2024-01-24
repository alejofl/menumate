export default class Report {
    constructor(
        comment,
        dateHandled,
        dateReported,
        handled,
        handlerId,
        handlerUrl,
        reportId,
        reporterId,
        reporterUrl,
        restaurantId,
        restaurantUrl,
        selfUrl
    ) {
        this.comment = comment;
        this.dateHandled = new Date(dateHandled);
        this.dateReported = new Date(dateReported);
        this.handled = handled;
        this.handlerId = handlerId;
        this.handlerUrl = handlerUrl;
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reporterUrl = reporterUrl;
        this.restaurantId = restaurantId;
        this.restaurantUrl = restaurantUrl;
        this.selfUrl = selfUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new Report(
            object.comment,
            object.dateHandled,
            object.dateReported,
            object.handled,
            object.handlerId,
            object.handlerUrl,
            object.reportId,
            object.reporterId,
            object.reporterUrl,
            object.restaurantId,
            object.restaurantUrl,
            object.selfUrl
        );
    }
}
