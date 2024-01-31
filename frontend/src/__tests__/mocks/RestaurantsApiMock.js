import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";
import {
    ACTIVATE_RESTAURANT_CONTENT_TYPE,
    REPORTS_CONTENT_TYPE,
    RESTAURANT_CATEGORIES_CONTENT_TYPE,
    RESTAURANT_DETAILS_CONTENT_TYPE,
    RESTAURANT_EMPLOYEES_CONTENT_TYPE, RESTAURANT_PRODUCT_NEW_CATEGORY_CONTENT_TYPE,
    RESTAURANT_PRODUCTS_CONTENT_TYPE,
    RESTAURANT_PROMOTIONS_CONTENT_TYPE, RESTAURANTS_CONTENT_TYPE,
    UNHANDLED_REPORTS_CONTENT_TYPE
} from "../../utils.js";

export const restaurantsHandlers = [
    http.get(apiUrl("/restaurants"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_DETAILS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "type": "restaurantDetailsDto",
                    "active": true,
                    "address": "1234 Olive Street, New York, NY",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories",
                    "dateCreated": "2023-05-11T20:08:20.463415",
                    "deleted": false,
                    "description": "A cozy bistro serving rustic Mediterranean cuisine. Enjoy homemade pastas, grilled seafood, and a curated wine list.",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/1/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/3",
                    "maxTables": 4,
                    "name": "Bistro Bliss",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=1",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/0",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/1",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/2",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/reports",
                    "restaurantId": 1,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=1",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                    "specialty": "breakfast",
                    "tags": [],
                    "averageProductPrice": 12.99,
                    "averageRating": 0.0,
                    "reviewCount": 0
                },
                {
                    "type": "restaurantDetailsDto",
                    "active": true,
                    "address": "5678 Sakura Avenue, Los Angeles, CA",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/2/categories",
                    "dateCreated": "2023-05-11T20:08:20.463954",
                    "deleted": false,
                    "description": "Dive into the world of sushi and sashimi with our fresh and creative Japanese culinary delights.",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/2/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/15",
                    "maxTables": 50,
                    "name": "Sushi Supreme",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=2",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/0",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/13",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/14",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/2/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/2/reports",
                    "restaurantId": 2,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=2",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/2",
                    "specialty": "argentine",
                    "tags": [],
                    "averageProductPrice": 15.99,
                    "averageRating": 0.0,
                    "reviewCount": 0
                },
                {
                    "type": "restaurantDetailsDto",
                    "active": true,
                    "address": "7890 Via Roma, Chicago, IL DESDE REACT",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories",
                    "dateCreated": "2023-05-11T20:08:20.464134",
                    "deleted": false,
                    "description": "Authentic Italian pizzeria serving thin-crust pizzas baked to perfection in a wood-fired oven. CAMBIADO CAMBIADO",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/3/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/30",
                    "maxTables": 5,
                    "name": "La Pizzeria Napoli camb",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=3",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/1",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/127",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/29",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/reports",
                    "restaurantId": 3,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=3",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "specialty": "desserts",
                    "tags": [
                        "locally_sourced",
                        "trendy",
                        "romantic",
                        "historic"
                    ],
                    "averageProductPrice": 27.461334,
                    "averageRating": 3.0,
                    "reviewCount": 7
                }
            ]);
        } else if (request.headers.get("Accept") === UNHANDLED_REPORTS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "active": true,
                    "address": "Ahora si va a funcionar",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/18/categories",
                    "dateCreated": "2024-01-25T23:29:42.542313",
                    "deleted": false,
                    "description": "Ahora si va a funcionar",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/18/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/124",
                    "maxTables": 12,
                    "name": "Ahora si va a funcionar",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=18",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/1",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/125",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/126",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/18/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/18/reports",
                    "restaurantId": 18,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=18",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/18",
                    "specialty": "asian",
                    "tags": [
                        "elegant",
                        "casual"
                    ],
                    "unhandledReportsCount": 1
                },
                {
                    "active": true,
                    "address": "7890 Via Roma, Chicago, IL DESDE REACT",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories",
                    "dateCreated": "2023-05-11T20:08:20.464134",
                    "deleted": false,
                    "description": "Authentic Italian pizzeria serving thin-crust pizzas baked to perfection in a wood-fired oven. CAMBIADO CAMBIADO",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/3/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/30",
                    "maxTables": 5,
                    "name": "La Pizzeria Napoli camb",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=3",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/1",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/127",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/29",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/reports",
                    "restaurantId": 3,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=3",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "specialty": "desserts",
                    "tags": [
                        "locally_sourced",
                        "trendy",
                        "romantic",
                        "historic"
                    ],
                    "unhandledReportsCount": 0
                },
                {
                    "active": false,
                    "address": "PRUEBA PRUEBA REACT",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/14/categories",
                    "dateCreated": "2024-01-22T00:05:09.482234",
                    "deleted": false,
                    "description": "PRUEBA PRUEBA REACT",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/14/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/111",
                    "maxTables": 1,
                    "name": "PRUEBA INACTIVE",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=14",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/1",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/112",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/113",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/14/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/14/reports",
                    "restaurantId": 14,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=14",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/14",
                    "specialty": "american",
                    "tags": [
                        "elegant",
                        "casual",
                        "cheap",
                        "cosy",
                        "family_friendly",
                        "kid_friendly",
                        "lgbt_friendly",
                        "pet_friendly",
                        "healthy"
                    ],
                    "unhandledReportsCount": 0
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/restaurants/:id"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_DETAILS_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "type": "restaurantDetailsDto",
                    "active": true,
                    "address": "1234 Olive Street, New York, NY",
                    "categoriesUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories",
                    "dateCreated": "2023-05-11T20:08:20.463415",
                    "deleted": false,
                    "description": "A cozy bistro serving rustic Mediterranean cuisine. Enjoy homemade pastas, grilled seafood, and a curated wine list.",
                    "employeesUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants/1/employees{/userId}",
                    "logoUrl": "http://localhost:8080/paw-2023a-01/api/images/3",
                    "maxTables": 4,
                    "name": "Bistro Bliss",
                    "ordersUrl": "http://localhost:8080/paw-2023a-01/api/orders?restaurantId=1",
                    "ownerUrl": "http://localhost:8080/paw-2023a-01/api/users/0",
                    "portrait1Url": "http://localhost:8080/paw-2023a-01/api/images/1",
                    "portrait2Url": "http://localhost:8080/paw-2023a-01/api/images/2",
                    "promotionsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/promotions",
                    "reportsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/reports",
                    "restaurantId": 1,
                    "reviewsUrl": "http://localhost:8080/paw-2023a-01/api/reviews?restaurantId=1",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                    "specialty": "breakfast",
                    "tags": [],
                    "averageProductPrice": 12.99,
                    "averageRating": 0.0,
                    "reviewCount": 0
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/restaurants/:id/categories"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_CATEGORIES_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "categoryId": 1,
                    "deleted": false,
                    "name": "Appetizers",
                    "orderNum": 1,
                    "productsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1/products",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1"
                },
                {
                    "categoryId": 2,
                    "deleted": false,
                    "name": "Salads",
                    "orderNum": 2,
                    "productsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/2/products",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/2"
                },
                {
                    "categoryId": 3,
                    "deleted": false,
                    "name": "Pasta",
                    "orderNum": 3,
                    "productsUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/3/products",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/3"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/restaurants/:restaurantId/categories/:categoryId/products"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_PRODUCTS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "available": true,
                    "categoryUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1",
                    "deleted": false,
                    "description": "Freshly toasted baguette slices with diced tomatoes, basil, garlic, and balsamic glaze.",
                    "imageUrl": "http://localhost:8080/paw-2023a-01/api/images/4",
                    "name": "Bruschetta Bonanza",
                    "price": 9.99,
                    "productId": 1,
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1/products/1"
                },
                {
                    "available": true,
                    "categoryUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1",
                    "deleted": false,
                    "description": "A flavorful platter of hummus, tabbouleh, stuffed grape leaves, olives, and pita bread.",
                    "imageUrl": "http://localhost:8080/paw-2023a-01/api/images/5",
                    "name": "Mediterranean Mezze",
                    "price": 11.99,
                    "productId": 2,
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1/products/2"
                },
                {
                    "available": true,
                    "categoryUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1",
                    "deleted": false,
                    "description": "Tender calamari rings lightly fried to perfection and served with lemon garlic aioli.",
                    "imageUrl": "http://localhost:8080/paw-2023a-01/api/images/6",
                    "name": "Crispy Calamari",
                    "price": 12.99,
                    "productId": 3,
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/1/products/3"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/restaurants/:id/promotions"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_PROMOTIONS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "destinationUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories/12/products/112",
                    "discountPercentage": 15.01,
                    "endDate": "2024-01-31T22:57:14.666",
                    "promotionId": 18,
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/promotions/18",
                    "sourceUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories/12/products/36",
                    "startDate": "2024-01-29T22:57:14.666"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/restaurants/:restaurantId/categories/:categoryId/products/:productId"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_PRODUCTS_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "available": true,
                    "categoryUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories/12",
                    "deleted": false,
                    "description": "Penne pasta cooked in a creamy tomato vodka sauce with a hint of spice and Parmesan cheese.",
                    "imageUrl": "http://localhost:8080/paw-2023a-01/api/images/45",
                    "name": "Penne alla Vodka",
                    "price": 11.89,
                    "productId": 112,
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories/12/products/112"
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.post(apiUrl("/restaurants"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANTS_CONTENT_TYPE) {
            return new HttpResponse(JSON.stringify({restaurantId: 1}), {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.post(apiUrl("/restaurants/:id/categories"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_CATEGORIES_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.post(apiUrl("/restaurants/:restaurantId/categories/:categoryId/products"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_PRODUCTS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.delete(apiUrl("/restaurants/:id"), () => {
        return new HttpResponse(null, {status: 204});
    }),

    http.patch(apiUrl("/restaurants/:id"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANTS_CONTENT_TYPE || request.headers.get("Content-Type") === ACTIVATE_RESTAURANT_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.patch(apiUrl("/restaurants/:restaurantId/categories/:categoryId"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_CATEGORIES_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.delete(apiUrl("/restaurants/:restaurantId/categories/:categoryId"), () => {
        return new HttpResponse(null, {status: 204});
    }),

    http.get(apiUrl("/restaurants/:id/employees"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_EMPLOYEES_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "email": "alejo@misterflores.com",
                    "name": "Alejo Flores",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "role": "owner",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/employees/1",
                    "userId": 1,
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
                },
                {
                    "email": "nllanos@itba.edu.ar",
                    "name": "Nehuen",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "role": "admin",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/employees/2",
                    "userId": 2,
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/2"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/restaurants/:restaurantId/employees/:userId"), ({request}) => {
        if (request.headers.get("Accept") === RESTAURANT_EMPLOYEES_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "email": "alejo.flores@sabf.org.ar",
                    "name": "alejo.flores",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "role": "orderhandler",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/employees/13",
                    "userId": 13,
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/13"
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.post(apiUrl("/restaurants/:id/employees"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_EMPLOYEES_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.delete(apiUrl("/restaurants/:restaurantId/employees/:userId"), () => {
        return new HttpResponse(null, {status: 204});
    }),

    http.put(apiUrl("/restaurants/:restaurantId/employees/:userId"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_EMPLOYEES_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.delete(apiUrl("/restaurants/:restaurantId/categories/:categoryId/products/:productId"), () => {
        return new HttpResponse(null, {status: 204});
    }),

    http.delete(apiUrl("/restaurants/:restaurantId/promotions/:promotionId"), () => {
        return new HttpResponse(null, {status: 204});
    }),

    http.post(apiUrl("/restaurants/:restaurantId/promotions"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_PROMOTIONS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.patch(apiUrl("/restaurants/:restaurantId/categories/:categoryId/products/:productId"), ({request}) => {
        if (request.headers.get("Content-Type") === RESTAURANT_PRODUCTS_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "available": true,
                    "categoryUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories/12",
                    "deleted": false,
                    "description": "Penne pasta cooked in a creamy tomato vodka sauce with a hint of spice and Parmesan cheese.",
                    "imageUrl": "http://localhost:8080/paw-2023a-01/api/images/45",
                    "name": "Penne alla Vodka",
                    "price": 11.89,
                    "productId": 112,
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3/categories/12/products/112"
                }
            );
        } else if (request.headers.get("Content-Type") === RESTAURANT_PRODUCT_NEW_CATEGORY_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.get(apiUrl("/restaurants/:id/reports"), ({request}) => {
        if (request.headers.get("Accept") === REPORTS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "comment": "A comment for the report",
                    "dateReported": "2024-01-28T04:45:24.042896",
                    "handled": false,
                    "reportId": 16,
                    "restaurantId": 18,
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/18",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/18/reports/16"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.post(apiUrl("/restaurants/:id/reports"), ({request}) => {
        if (request.headers.get("Content-Type") === REPORTS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.patch(apiUrl("/restaurants/:restaurantId/reports/:reportId"), () => {
        return new HttpResponse(null, {status: 204});
    })
];
