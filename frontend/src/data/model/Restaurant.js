import UriTemplate from "uri-templates";

export default class Restaurant {
    constructor(
        active,
        address,
        categoriesUrl,
        dateCreated,
        deleted,
        description,
        employeesUriTemplate,
        logoUrl,
        maxTables,
        name,
        ordersUrl,
        ownerUrl,
        portrait1Url,
        portrait2Url,
        promotionsUrl,
        reportsUrl,
        restaurantId,
        reviewsUrl,
        selfUrl,
        specialty,
        tags,
        averageProductPrice,
        averageRating,
        reviewCount,
        dineInCompletionTime,
        takeAwayCompletionTime,
        deliveryCompletionTime,
        pendingOrderCount,
        unhandledReportsCount
    ) {
        this.active = active;
        this.address = address;
        this.categoriesUrl = categoriesUrl;
        this.dateCreated = new Date(dateCreated);
        this.deleted = deleted;
        this.description = description;
        this.employeesUriTemplate = UriTemplate(employeesUriTemplate);
        this.logoUrl = logoUrl;
        this.maxTables = maxTables;
        this.name = name;
        this.ordersUrl = ordersUrl;
        this.ownerUrl = ownerUrl;
        this.portrait1Url = portrait1Url;
        this.portrait2Url = portrait2Url;
        this.promotionsUrl = promotionsUrl;
        this.reportsUrl = reportsUrl;
        this.restaurantId = restaurantId;
        this.reviewsUrl = reviewsUrl;
        this.selfUrl = selfUrl;
        this.specialty = specialty;
        this.tags = tags;
        this.averageProductPrice = averageProductPrice;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
        this.dineInCompletionTime = dineInCompletionTime;
        this.takeAwayCompletionTime = takeAwayCompletionTime;
        this.deliveryCompletionTime = deliveryCompletionTime;
        this.pendingOrderCount = pendingOrderCount;
        this.unhandledReportsCount = unhandledReportsCount;
    }

    static fromJSON(object) {
        return new Restaurant(
            object.active,
            object.address,
            object.categoriesUrl,
            object.dateCreated,
            object.deleted,
            object.description,
            object.employeesUriTemplate,
            object.logoUrl,
            object.maxTables,
            object.name,
            object.ordersUrl,
            object.ownerUrl,
            object.portrait1Url,
            object.portrait2Url,
            object.promotionsUrl,
            object.reportsUrl,
            object.restaurantId,
            object.reviewsUrl,
            object.selfUrl,
            object.specialty,
            object.tags,
            object.averageProductPrice,
            object.averageRating,
            object.reviewCount,
            object.dineInCompletionTime,
            object.takeAwayCompletionTime,
            object.deliveryCompletionTime,
            object.pendingOrderCount,
            object.unhandledReportsCount
        );
    }
}
