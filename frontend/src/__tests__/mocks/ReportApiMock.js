import {http, HttpResponse} from "msw";
import {apiUrl} from "../setup/utils.js";
import {REPORTS_CONTENT_TYPE} from "../../utils.js";


export const reportHandlers = [
    http.get(apiUrl("/restaurants/:id/reports"), ({request}) => {
        if (request.headers.get("Accept") === REPORTS_CONTENT_TYPE) {
            return HttpResponse.json([
                {
                    "comment": "Comment",
                    "dateHandled": "2023-05-16T16:54:14.764599",
                    "dateReported": "2023-05-16T16:54:14.764599",
                    "handled": true,
                    "handlerId": 1,
                    "handlerUrl": "URL",
                    "reportId": 1,
                    "reporterId": 1,
                    "reporterUrl": "URL",
                    "restaurantId": 2,
                    "restaurantUrl": "URL",
                    "selfUrl": "URL"
                },
                {
                    "comment": "Comment2",
                    "dateHandled": "2023-05-16T16:54:14.764599",
                    "dateReported": "2023-05-16T16:54:14.764599",
                    "handled": true,
                    "handlerId": 1,
                    "handlerUrl": "URL",
                    "reportId": 1,
                    "reporterId": 1,
                    "reporterUrl": "URL",
                    "restaurantId": 2,
                    "restaurantUrl": "URL",
                    "selfUrl": "URL"
                }
            ]);
        } else {
            return new HttpResponse(null, {status: 406});
        }
    })
];
