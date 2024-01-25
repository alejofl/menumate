import React, { useEffect, useState } from "react";
import Api from "../data/Api.js";
import UriTemplate from "uri-templates";

const ApiContext = React.createContext({
    didDiscovery: false,
    ordersUrl: "",
    restaurantsUrl: "",
    restaurantsUriTemplate: "",
    reviewsUrl: "",
    reviewsUriTemplate: "",
    usersUrl: "",
    imagesUrl: ""
});

export function ApiContextProvider({children}) {
    const [didDiscovery, setDidDiscovery] = useState(false);
    const [failedDiscovery, setFailedDiscovery] = useState(false);
    const [ordersUrl, setOrdersUrl] = useState("");
    const [restaurantsUriTemplateString, setRestaurantsUriTemplateString] = useState("");
    const [reviewsUriTemplateString, setReviewsUriTemplateString] = useState("");
    const [usersUrl, setUsersUrl] = useState("");
    const [imagesUrl, setImagesUrl] = useState("");

    useEffect(() => {
        Api.get("/")
            .then(results => {
                console.log(results);
                setOrdersUrl(results.data.ordersUrl);
                setRestaurantsUriTemplateString(results.data.restaurantsUriTemplate);
                setReviewsUriTemplateString(results.data.reviewsUriTemplate);
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
                restaurantsUriTemplate: UriTemplate(restaurantsUriTemplateString),
                restaurantsUrl: UriTemplate(restaurantsUriTemplateString).fill({}),
                reviewsUriTemplate: UriTemplate(reviewsUriTemplateString),
                reviewsUrl: UriTemplate(reviewsUriTemplateString).fill({}),
                usersUrl: usersUrl,
                imagesUrl: imagesUrl
            }}>
                {children}
            </ApiContext.Provider>
        </>
    );
}

export default ApiContext;
