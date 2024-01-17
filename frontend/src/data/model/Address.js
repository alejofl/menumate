export default class Address {
    constructor(address, lastUsed, name, selfUrl) {
        this.address = address;
        this.lastUsed = new Date(lastUsed);
        this.name = name;
        this.selfUrl = selfUrl;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new Address(
            object.address,
            object.lastUsed,
            object.name,
            object.selfUrl
        );
    }
}
