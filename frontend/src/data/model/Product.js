export default class Product {
    constructor(available, categoryUrl, deleted, description, imageUrl, name, price, productId, selfUrl) {
        this.available = available;
        this.categoryUrl = categoryUrl;
        this.deleted = deleted;
        this.description = description;
        this.imageUrl = imageUrl;
        this.name = name;
        this.price = price;
        this.productId = productId;
        this.selfUrl = selfUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new Product(
            object.available,
            object.categoryUrl,
            object.deleted,
            object.description,
            object.imageUrl,
            object.name,
            object.price,
            object.productId,
            object.selfUrl
        );
    }
}
