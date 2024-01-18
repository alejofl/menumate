export default class User {
    constructor(
        addressesUrl,
        dateJoined,
        email,
        isActive,
        name,
        ordersUrl,
        preferredLanguage,
        restaurantsEmployedAtUrl,
        reviewsUrl,
        role,
        selfUrl
    ) {
        this.addressesUrl = addressesUrl;
        this.dateJoined = new Date(dateJoined);
        this.email = email;
        this.isActive = isActive;
        this.name = name;
        this.ordersUrl = ordersUrl;
        this.preferredLanguage = preferredLanguage;
        this.restaurantsEmployedAtUrl = restaurantsEmployedAtUrl;
        this.reviewsUrl = reviewsUrl;
        this.role = role;
        this.selfUrl = selfUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new User(
            object.addressesUrl,
            object.dateJoined,
            object.email,
            object.isActive,
            object.name,
            object.ordersUrl,
            object.preferredLanguage,
            object.restaurantsEmployedAtUrl,
            object.reviewsUrl,
            object.role,
            object.selfUrl
        );
    }
}
