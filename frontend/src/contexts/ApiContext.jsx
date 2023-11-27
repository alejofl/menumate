import React, { useEffect, useState } from "react";
import Api from "../data/api/Api.js";

const ApiContext = React.createContext({
    ordersUrl: "",
    restaurantsUrl: "",
    reviewsUrl: "",
    usersUrl: ""
});

export function ApiContextProvider({children}) {
    const [ordersUrl, setOrdersUrl] = useState("");
    const [restaurantsUrl, setRestaurantsUrl] = useState("");
    const [reviewsUrl, setReviewsUrl] = useState("");
    const [usersUrl, setUsersUrl] = useState("");

    useEffect(() => {
        Api.get("/")
            .then(results => {
                setOrdersUrl(results.data.ordersUrl);
                setRestaurantsUrl(results.data.restaurantsUrl);
                setReviewsUrl(results.data.reviewsUrl);
                setUsersUrl(results.data.usersUrl);
            });
    });

    return (
        <>
            <ApiContext.Provider value={{
                ordersUrl: ordersUrl,
                restaurantsUrl: restaurantsUrl,
                reviewsUrl: reviewsUrl,
                usersUrl: usersUrl
            }}>
                {children}
            </ApiContext.Provider>
        </>
    );
}

export default ApiContext;
