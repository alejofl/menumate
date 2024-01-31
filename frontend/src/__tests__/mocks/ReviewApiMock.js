import {http, HttpResponse} from "msw";
import {apiUrl} from "../setup/utils.js";
import {REVIEW_CONTENT_TYPE, REVIEW_REPLY_CONTENT_TYPE} from "../../utils.js";


export const reviewHandlers = [
    http.get(apiUrl("/reviews"), ({request}) => {
        if (request.headers.get("Accept") === REVIEW_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "comment": "Comment",
                    "date": "2023-05-16T16:54:14.764599",
                    "orderId": 1,
                    "orderUrl": "URL",
                    "rating": 3,
                    "reviewerName": "UserName",
                    "reply": "Reply",
                    "selfUrl": "URL"
                },
                {
                    "comment": "Comment2",
                    "date": "2023-05-16T16:54:14.764599",
                    "orderId": 2,
                    "orderUrl": "URL",
                    "rating": 4,
                    "reviewerName": "UserName2",
                    "reply": "Reply2",
                    "selfUrl": "URL"
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
                    "comment": "Comment",
                    "date": "2023-05-16T16:54:14.764599",
                    "orderId": 1,
                    "orderUrl": "URL",
                    "rating": 3,
                    "reviewerName": "UserName",
                    "reply": "Reply",
                    "selfUrl": "URL"
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
