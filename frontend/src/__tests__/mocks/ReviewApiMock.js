import {http, HttpResponse} from "msw";
import {apiUrl} from "../setup/utils.js";
import {REVIEW_CONTENT_TYPE, REVIEW_REPLY_CONTENT_TYPE} from "../../utils.js";


export const reviewHandlers = [
    http.get(apiUrl("/reviews"), ({request}) => {
        if (request.headers.get("Accept") === REVIEW_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "comment": "Prueba prueba",
                    "date": "2023-05-29T16:00:03.797726",
                    "orderId": 8,
                    "orderUrl": "http://localhost:8080/paw-2023a-01/api/orders/8",
                    "rating": 4,
                    "reviewerName": "Alejo Flores",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/reviews/8"
                },
                {
                    "comment": "<script>alert(\"Hola\");</script>",
                    "date": "2023-05-16T17:18:35.916966",
                    "orderId": 5,
                    "orderUrl": "http://localhost:8080/paw-2023a-01/api/orders/5",
                    "rating": 5,
                    "reviewerName": "Alejo Flores",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/reviews/5"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.patch(apiUrl("/reviews/:id"), ({request}) => {
        if (request.headers.get("Content-Type") === REVIEW_REPLY_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 204});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    }),

    http.get(apiUrl("/reviews/:id"), ({request}) => {
        if (request.headers.get("Accept") === REVIEW_CONTENT_TYPE) {
            return HttpResponse.json(
                {
                    "comment": "rwtgq3g5qr4t53",
                    "date": "2023-06-10T01:16:05.425389",
                    "orderId": 10,
                    "orderUrl": "http://localhost:8080/paw-2023a-01/api/orders/10",
                    "rating": 2,
                    "reply": "FUNCIONAAAAA",
                    "reviewerName": "Alejo Flores",
                    "selfUrl": "http://localhost:8080/paw-2023a-01/api/reviews/10"
                }
            );
        } else {
            return new HttpResponse(null, {status: 406});
        }
    }),

    http.post(apiUrl("/reviews"), ({request}) => {
        if (request.headers.get("Content-Type") === REVIEW_CONTENT_TYPE) {
            return new HttpResponse(null, {status: 201});
        } else {
            return new HttpResponse(null, {status: 415});
        }
    })
];
