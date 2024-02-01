import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";
import {ORDER_ITEMS_CONTENT_TYPE, ORDERS_CONTENT_TYPE} from "../../utils.js";

export const ordersHandlers = [
    http.post(apiUrl("/orders"), ({request}) => {
        if (request.headers.get("Content-Type") === ORDERS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.get(apiUrl("/orders/:id"), ({request, params}) => {
        if (request.headers.get("Accept") === ORDERS_CONTENT_TYPE) {
            return HttpResponse.json({
                "address": "Julian Alvarez 2623",
                "dateOrdered": "2024-01-28T12:51:07.576997",
                "itemsUrl": "http://localhost:8080/paw-2023a-01/api/orders/32/items",
                "orderId": parseInt(params.id),
                "orderType": "delivery",
                "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                "selfUrl": "http://localhost:8080/paw-2023a-01/api/orders/32",
                "status": "pending",
                "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
            });
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/orders/:id/items"), ({request}) => {
        if (request.headers.get("Accept") === ORDER_ITEMS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "lineNumber": 1,
                    "productUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/2/products/4",
                    "quantity": 1
                },
                {
                    "lineNumber": 2,
                    "productUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1/categories/3/products/8",
                    "quantity": 1
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.get(apiUrl("/orders"), ({request}) => {
        if (request.headers.get("Accept") === ORDERS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "address": "Julian Alvarez 2623",
                    "dateOrdered": "2024-01-28T12:51:07.576997",
                    "itemsUrl": "http://localhost:8080/paw-2023a-01/api/orders/32/items",
                    "orderId": 32,
                    "orderType": "delivery",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/1",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/orders/32",
                    "status": "pending",
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
                },
                {
                    "dateOrdered": "2024-01-17T18:20:37.003379",
                    "itemsUrl": "http://localhost:8080/paw-2023a-01/api/orders/31/items",
                    "orderId": 31,
                    "orderType": "dinein",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/orders/31",
                    "status": "pending",
                    "tableNumber": 12000,
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
                },
                {
                    "address": "Julian Alvarez 2623",
                    "dateOrdered": "2024-01-17T18:16:18.825544",
                    "itemsUrl": "http://localhost:8080/paw-2023a-01/api/orders/30/items",
                    "orderId": 30,
                    "orderType": "delivery",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/orders/30",
                    "status": "pending",
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
                },
                {
                    "dateOrdered": "2024-01-17T18:13:47.163233",
                    "itemsUrl": "http://localhost:8080/paw-2023a-01/api/orders/29/items",
                    "orderId": 29,
                    "orderType": "takeaway",
                    "restaurantUrl": "http://localhost:8080/paw-2023a-01/api/restaurants/3",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/orders/29",
                    "status": "pending",
                    "userUrl": "http://localhost:8080/paw-2023a-01/api/users/1"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.patch(apiUrl("/orders/:id"), ({request}) => {
        if (request.headers.get("Content-Type") === ORDERS_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    })
];
