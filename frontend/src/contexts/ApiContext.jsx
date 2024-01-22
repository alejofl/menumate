import React, { useEffect, useState } from "react";
import Api from "../data/Api.js";

const ApiContext = React.createContext({
    didDiscovery: false,
    ordersUrl: "",
    restaurantsUrl: "",
    reviewsUrl: "",
    usersUrl: "",
    imagesUrl: ""
});

export function ApiContextProvider({children}) {
    const [didDiscovery, setDidDiscovery] = useState(false);
    const [failedDiscovery, setFailedDiscovery] = useState(false);
    const [ordersUrl, setOrdersUrl] = useState("");
    const [restaurantsUrl, setRestaurantsUrl] = useState("");
    const [reviewsUrl, setReviewsUrl] = useState("");
    const [usersUrl, setUsersUrl] = useState("");
    const [imagesUrl, setImagesUrl] = useState("");

    useEffect(() => {
        Api.get("/")
            .then(results => {
                setOrdersUrl(results.data.ordersUrl);
                setRestaurantsUrl(results.data.restaurantsUrl);
                setReviewsUrl(results.data.reviewsUrl);
                setUsersUrl(results.data.usersUrl);
                setImagesUrl(results.data.imagesUrl);
            })
            .catch(() => setFailedDiscovery(true))
            .finally(() => setDidDiscovery(true));
    });

    return (
        <>
            <ApiContext.Provider value={{
                didDiscovery: didDiscovery,
                failedDiscovery: failedDiscovery,
                ordersUrl: ordersUrl,
                restaurantsUrl: restaurantsUrl,
                reviewsUrl: reviewsUrl,
                usersUrl: usersUrl,
                imagesUrl: imagesUrl
            }}>
                {children}
            </ApiContext.Provider>
        </>
    );
}

export default ApiContext;
