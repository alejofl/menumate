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
    http.post(apiUrl(""), ({request}) => {
        if (request.headers.get("Content-Type") === USER_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

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
        if (request.headers.get("Content-Type") === USER_CONTENT_TYPE || request.headers.get("Content-Type") === USER_ROLE_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.get(apiUrl("/users/:id"), ({request, params}) => {
        if (request.headers.get("Accept") === USER_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "addressesUrl": "http://localhost:8080/paw-2023a-01/api/users/1/addresses",
                    "dateJoined": "2023-05-11T20:08:20.462976",
                    "email": "alejo@misterflores.com",
                    "isActive": true,
                    "name": "Alejo Flores",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?userId=1",
                    "preferredLanguage": "es",
                    "restaurantsEmployedAtUrl": "http://localhost:8080/paw-2023a-01/api/restaurants?forEmployeeId=1",
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?userId=1",
                    "role": "moderator",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/users/1",
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
                    "addressesUrl": "http://localhost:8080/paw-2023a-01/api/users/2/addresses",
                    "dateJoined": "2023-05-11T20:08:20.463146",
                    "email": "nllanos@itba.edu.ar",
                    "isActive": true,
                    "name": "Nehuen",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?userId=2",
                    "preferredLanguage": "en",
                    "restaurantsEmployedAtUrl": "http://localhost:8080/paw-2023a-01/api/restaurants?forEmployeeId=2",
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?userId=2",
                    "role": "moderator",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/users/2",
                    "userId": 2
                },
                {
                    "addressesUrl": "http://localhost:8080/paw-2023a-01/api/users/1/addresses",
                    "dateJoined": "2023-05-11T20:08:20.462976",
                    "email": "alejo@misterflores.com",
                    "isActive": true,
                    "name": "Alejo Flores",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?userId=1",
                    "preferredLanguage": "es",
                    "restaurantsEmployedAtUrl": "http://localhost:8080/paw-2023a-01/api/restaurants?forEmployeeId=1",
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?userId=1",
                    "role": "moderator",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/users/1",
                    "userId": 1
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
                    "address": "Julian Alvarez 2623",
                    "lastUsed": "2024-01-28T12:51:07.600",
                    "name": "Nehui",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/users/1/addresses/2"
                },
                {
                    "address": "prueba",
                    "lastUsed": "2024-01-24T13:33:28.744",
                    "name": "Prueba",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/users/1/addresses/6"
                },
                {
                    "address": "Edison 439, Martinez",
                    "lastUsed": "2024-01-16T02:32:12.289848",
                    "name": "Casa",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/users/1/addresses/1"
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
                    "email": "alejo@misterflores.com",
                    "name": "Alejo Flores",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/2",
                    "role": "admin",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/2/employees/1",
                    "userId": 1,
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.delete(apiUrl("/users/:id"), () => {
        return new HttpResponse(null, {status: 204});
    })
];
