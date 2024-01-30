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
    })
];
