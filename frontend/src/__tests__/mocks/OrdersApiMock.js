import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";
import {ORDERS_CONTENT_TYPE} from "../../utils.js";

export const ordersHandlers = [
    http.get(apiUrl("/orders/:id"), ({request, params}) => {
        if (request.headers.get("Accept") === ORDERS_CONTENT_TYPE) {
            return HttpResponse.json({
                "dateOrdered": "2023-05-16T16:54:14.764599",
                "itemsUrl": "URL",
                "orderId": params.id,
                "orderType": "takeaway",
                "restaurantUrl": "URL",
                "selfUrl": "URL",
                "status": "pending",
                "userUrl": "URL"
            });
        } else {
            return new HttpResponse(null, {status: 406});
        }
    })
];
