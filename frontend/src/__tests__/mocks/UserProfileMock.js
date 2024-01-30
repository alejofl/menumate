import {http, HttpResponse} from "msw";
import {apiUrl} from "../setup/utils.js";
import {
    RESTAURANT_EMPLOYEES_CONTENT_TYPE,
    USER_ADDRESS_CONTENT_TYPE,
    USER_CONTENT_TYPE,
    USER_PASSWORD_CONTENT_TYPE,
    USER_ROLE_CONTENT_TYPE
} from "../../utils.js";

export const userHandler = [
    http.post(apiUrl("/users/:id"), ({request}) => {
        if (request.headers.get("Content-Type") === USER_PASSWORD_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.patch(apiUrl("/users/:id"), ({request}) => {
        if (request.headers.get("Content-Type") === USER_PASSWORD_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.post(apiUrl("/users"), ({request}) => {
        if (request.headers.get("Content-Type") === USER_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.get(apiUrl("/users/:id"), ({request, params}) => {
        if (request.headers.get("Accept") === USER_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "addressesUrl": "URL",
                    "dateJoined": "2024-01-30T16:59:49.591",
                    "email": "user@gmail.com",
                    "isActive": true,
                    "name": "User",
                    "ordersUrl": "URL",
                    "preferredLanguage": "en",
                    "restaurantsEmployedAtUrl": "URL",
                    "reviewsUrl": "URL",
                    "role": "ROLE_MODERATOR",
                    "selfUrl": "URL",
                    "userId": parseInt(params.id)
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/users"), ({request}) => {
        if (request.headers.get("Accept") === USER_ROLE_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "addressesUrl": "URL",
                    "dateJoined": "2024-01-30T16:59:49.591",
                    "email": "user1@gmail.com",
                    "isActive": true,
                    "name": "User1",
                    "ordersUrl": "URL",
                    "preferredLanguage": "en",
                    "restaurantsEmployedAtUrl": "URL",
                    "reviewsUrl": "URL",
                    "role": "ROLE_MODERATOR",
                    "selfUrl": "URL",
                    "userId": 1
                },
                {
                    "addressesUrl": "URL",
                    "dateJoined": "2024-01-30T16:59:49.591",
                    "email": "user2@gmail.com",
                    "isActive": true,
                    "name": "User2",
                    "ordersUrl": "URL",
                    "preferredLanguage": "en",
                    "restaurantsEmployedAtUrl": "URL",
                    "reviewsUrl": "URL",
                    "role": "ROLE_MODERATOR",
                    "selfUrl": "URL",
                    "userId": 2
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.post(apiUrl("/users/:id/addresses"), ({request}) => {
        if (request.headers.get("Content-Type") === USER_ADDRESS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.get(apiUrl("/users/:id/addresses"), ({request}) => {
        if (request.headers.get("Accept") === USER_ADDRESS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "address": "Arenales 100",
                    "lastUsed": "2024-01-30T16:59:49.591",
                    "name": "Casa",
                    "selfUrl": "URL"
                },
                {
                    "address": "Bulnes 201",
                    "lastUsed": "2024-02-30T16:59:49.591",
                    "name": "Trabajo",
                    "selfUrl": "URL"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.patch(apiUrl("/users/:id/addresses/:addressId"), ({request}) => {
        if (request.headers.get("Content-Type") === USER_ADDRESS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.delete(apiUrl("/users/:id/addresses/:addressId"), () => {
        return new HttpResponse(null, {status: 204});
    }),

    http.get(apiUrl("restaurants/:restaurantId/employees/:userId"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_EMPLOYEES_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "email": "user@gmail.com",
                    "name": "User",
                    "restaurantUrl": "URL",
                    "role": "owner",
                    "selfUrl": "URL",
                    "userUrl": "URL"
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.post(apiUrl("restaurants/:restaurantId/employees"), ({request}) => {
        if (request.headers.get("Content-Type") === USER_ROLE_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.delete(apiUrl("restaurants/:restaurantId/employees/:userId"), () => {
        return new HttpResponse(null, {status: 204});
    })
];
