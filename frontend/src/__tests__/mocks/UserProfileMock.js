import {http, HttpResponse} from "msw";
import {apiUrl} from "../setup/utils.js";
import {USER_ADDRESS_CONTENT_TYPE} from "../../utils.js";

export const userHandler = [
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
    })
];
