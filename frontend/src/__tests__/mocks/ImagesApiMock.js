import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";

export const imagesHandlers = [
    http.post(apiUrl("/images"), () => {
        return new HttpResponse(
            JSON.stringify({
                "imageId": 45,
                "selfUri": "http://localhost:8080/paw-2023a-01/api/images/45"
            }),
            {
                status: 201
            }
        );
    }),

    http.options(apiUrl("/images"), () => {
        return new HttpResponse(null, {status: 200});
    })
];
