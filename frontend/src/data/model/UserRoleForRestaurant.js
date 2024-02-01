import {ROLE_FOR_RESTAURANT} from "../../utils.js";

export default class UserRoleForRestaurant {
    constructor(email, name, restaurantUrl, role, selfUrl, userUrl) {
        this.email = email;
        this.name = name;
        this.restaurantUrl = restaurantUrl;
        this.role = role;
        this.selfUrl = selfUrl;
        this.userUrl = userUrl;

        this.isOwner = role === ROLE_FOR_RESTAURANT.OWNER;
        this.isAdmin = role === ROLE_FOR_RESTAURANT.ADMIN || this.isOwner;
        this.isOrderHandler = role === ROLE_FOR_RESTAURANT.ORDER_HANDLER || this.isOwner || this.isAdmin;
    }

    // Static method to create an instance from a JSON object
    static fromJSON(object) {
        return new UserRoleForRestaurant(
            object.email,
            object.name,
            object.restaurantUrl,
            object.role,
            object.selfUrl,
            object.userUrl
        );
    }

    static notEmployee() {
        return new UserRoleForRestaurant(
            null,
            null,
            null,
            null,
            null,
            null
        );
    }
}
