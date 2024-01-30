import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";

export const homeIndexHandlers = [
    http.get(apiUrl("/"), () => {
        return HttpResponse.json(
            {
                "imagesUrl": "http://localhost:8080/paw-2023a-01/api/images",
                "ordersUriTemplate": "http://localhost:8080/paw-2023a-01/api/orders{/orderId}",
                "restaurantsUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants{/restaurantId}",
                "reviewsUriTemplate": "http://localhost:8080/paw-2023a-01/api/reviews{/orderId}",
                "usersUrl": "http://localhost:8080/paw-2023a-01/api/users"
            }
        );
    })
];
