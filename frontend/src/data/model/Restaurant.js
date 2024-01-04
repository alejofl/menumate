export default class Restaurant {
    constructor(
        active,
        address,
        categoriesUrl,
        dateCreated,
        deleted,
        description,
        employeesUrl,
        logoUrl,
        maxTables,
        name,
        ordersUrl,
        ownerUrl,
        portrait1Url,
        portrait2Url,
        restaurantId,
        reviewsUrl,
        selfUrl,
        specialty,
        tags,
        averageProductPrice,
        averageRating,
        reviewCount
    ) {
        this.active = active;
        this.address = address;
        this.categoriesUrl = categoriesUrl;
        this.dateCreated = new Date(dateCreated);
        this.deleted = deleted;
        this.description = description;
        this.employeesUrl = employeesUrl;
        this.logoUrl = logoUrl;
        this.maxTables = maxTables;
        this.name = name;
        this.ordersUrl = ordersUrl;
        this.ownerUrl = ownerUrl;
        this.portrait1Url = portrait1Url;
        this.portrait2Url = portrait2Url;
        this.restaurantId = restaurantId;
        this.reviewsUrl = reviewsUrl;
        this.selfUrl = selfUrl;
        this.specialty = specialty;
        this.tags = tags;
        this.averageProductPrice = averageProductPrice;
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    static fromJSON(object) {
        return new Restaurant(
            object.active,
            object.address,
            object.categoriesUrl,
            object.dateCreated,
            object.deleted,
            object.description,
            object.employeesUrl,
            object.logoUrl,
            object.maxTables,
            object.name,
            object.ordersUrl,
            object.ownerUrl,
            object.portrait1Url,
            object.portrait2Url,
            object.restaurantId,
            object.reviewsUrl,
            object.selfUrl,
            object.specialty,
            object.tags,
            object.averageProductPrice,
            object.averageRating,
            object.reviewCount
        );
    }
}
