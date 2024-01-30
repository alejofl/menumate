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
                "dateOrdered": "2023-05-16T16:54:14.764599",
                "itemsUrl": "URL",
                "orderId": parseInt(params.id),
                "orderType": "takeaway",
                "restaurantUrl": "URL",
                "selfUrl": "URL",
                "status": "pending",
                "userUrl": "URL"
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
                    "productUrl": "URL",
                    "quantity": 4
                },
                {
                    "lineNumber": 2,
                    "productUrl": "URL",
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
                    "dateConfirmed": "2023-05-13T13:38:44.213727",
                    "dateDelivered": "2023-05-13T13:38:50.054667",
                    "dateOrdered": "2023-05-13T13:38:28.798012",
                    "dateReady": "2023-05-13T13:38:47.026651",
                    "itemsUrl": "URL",
                    "orderId": 1,
                    "orderType": "takeaway",
                    "restaurantUrl": "URL",
                    "selfUrl": "URL",
                    "status": "delivered",
                    "userUrl": "URL"
                },
                {
                    "dateConfirmed": "2023-05-23T18:35:27.058598",
                    "dateDelivered": "2023-05-23T18:35:32.357684",
                    "dateOrdered": "2023-05-23T18:34:58.691331",
                    "dateReady": "2023-05-23T18:35:30.087099",
                    "itemsUrl": "URL",
                    "orderId": 7,
                    "orderType": "takeaway",
                    "restaurantUrl": "URL",
                    "selfUrl": "URL",
                    "status": "delivered",
                    "userUrl": "URL"
                },
                {
                    "address": "Edison 439, Martinez",
                    "dateConfirmed": "2023-06-10T01:03:45.335",
                    "dateDelivered": "2023-06-10T01:04:03.749",
                    "dateOrdered": "2023-06-05T11:11:59.161896",
                    "dateReady": "2023-06-10T01:04:00.736",
                    "itemsUrl": "URL",
                    "orderId": 10,
                    "orderType": "delivery",
                    "restaurantUrl": "URL",
                    "selfUrl": "URL",
                    "status": "delivered",
                    "userUrl": "URL"
                },
                {
                    "address": "---,Garay 415, Quilmes",
                    "dateConfirmed": "2023-06-10T01:03:47.834",
                    "dateDelivered": "2023-06-10T01:04:07.308",
                    "dateOrdered": "2023-06-05T19:30:03.066291",
                    "dateReady": "2023-06-10T01:03:55.512",
                    "itemsUrl": "URL",
                    "orderId": 12,
                    "orderType": "delivery",
                    "restaurantUrl": "URL",
                    "selfUrl": "URL",
                    "status": "delivered",
                    "userUrl": "URL"
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
