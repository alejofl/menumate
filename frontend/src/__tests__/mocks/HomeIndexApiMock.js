import { http, HttpResponse } from "msw";
import {apiUrl, DUMMY_AUTH_TOKEN, DUMMY_REFRESH_TOKEN} from "../setup/utils.js";

export const homeIndexHandlers = [
    http.get(apiUrl("/"), ({request}) => {
        return new HttpResponse(
            JSON.stringify({
                "imagesUrl": "http://localhost:8080/paw-2023a-01/api/images",
                "ordersUriTemplate": "http://localhost:8080/paw-2023a-01/api/orders{/orderId}",
                "restaurantsUriTemplate": "http://localhost:8080/paw-2023a-01/api/restaurants{/restaurantId}",
                "reviewsUriTemplate": "http://localhost:8080/paw-2023a-01/api/reviews{/orderId}",
                "usersUrl": "http://localhost:8080/paw-2023a-01/api/users"
            }),
            {
                headers: {
                    ...(
                        request.headers.get("Authorization")
                            ?
                            {
                                "x-menumate-authtoken": DUMMY_AUTH_TOKEN,
                                "x-menumate-refreshtoken": DUMMY_REFRESH_TOKEN
                            }
                            :
                            {}
                    )
                }
            }
        );
    })
];
