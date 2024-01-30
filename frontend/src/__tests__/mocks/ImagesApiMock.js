import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";

export const imagesHandlers = [
    http.post(apiUrl("/images"), ({request}) => {
        if (request.headers.get("Content-Type") === "multipart/form-data") {
            return new HttpResponse(
                {
                    "id": 45,
                    "url": "http://localhost:8080/paw-2023a-01/api/images/45"
                },
                {
                    status: 201
                }
            );
        }
    })
];
