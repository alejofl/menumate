import { http, HttpResponse } from "msw";
import {apiUrl} from "../setup/utils.js";

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
                                "x-menumate-authtoken": "eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJNT0NLLUFDQ0VTUyIsIm5hbWUiOiJKb2huIERvZSIsInJvbGUiOiJNT0RFUkFUT1IiLCJzZWxmVXJsIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3Bhdy0yMDIzYS0wMS9hcGkvdXNlcnMvMSIsInN1YiI6ImFsZWpvQG1pc3RlcmZsb3Jlcy5jb20iLCJpYXQiOjE3MDY3MTk1MzMsImV4cCI6MTcwNjcyMDEzM30.lj53LaO6g6oGfY0dz9pFC31kZpnvMSUAsmWtgV1MA4AuWiTh3ykduVY52ljJXCR2Acgb3BjgAa1PhhvaH5OBmA",
                                "x-menumate-refreshtoken": "eyJhbGciOiJIUzUxMiJ9.eyJ0b2tlblR5cGUiOiJNT0NLIFJFRlJFU0giLCJzdWIiOiJhbGVqb0BtaXN0ZXJmbG9yZXMuY29tIiwiaWF0IjoxNzA2NzE5NTMzLCJleHAiOjE3MDczMjQzMzN9.FUcyHdDNSm3brBW-QVPuA45nHVACPzQ6Q46gzAk7Z82bIhIFhxKkZe27nVcMaJSxlQWufZpUtx5a8ydoMUu16A"
                            }
                            :
                            {}
                    )
                }
            }
        );
    })
];
