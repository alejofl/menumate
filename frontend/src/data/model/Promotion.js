export default class Promotion {
    constructor(destinationUrl, discountPercentage, endDate, selfUrl, sourceUrl, startDate) {
        this.destinationUrl = destinationUrl;
        this.discountPercentage = discountPercentage;
        this.endDate = new Date(endDate);
        this.selfUrl = selfUrl;
        this.sourceUrl = sourceUrl;
        this.startDate = new Date(startDate);
    }

    static fromJSON(object) {
        return new Promotion(
            object.destinationUrl,
            object.discountPercentage,
            object.endDate,
            object.selfUrl,
            object.sourceUrl,
            object.startDate
        );
    }
}
