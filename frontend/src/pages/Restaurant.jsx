import {useParams} from "react-router-dom";
import Page from "../components/Page.jsx";
import {useQuery} from "@tanstack/react-query";
import {useTranslation} from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import {useContext} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import "./styles/restaurant.styles.css";
import Rating from "../components/Rating.jsx";
import TagsContainer from "../components/TagsContainer.jsx";

function Restaurant() {
    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const restaurantService = useRestaurantService(api);

    const { restaurantId } = useParams();
    const { isLoading, isError, data, error } = useQuery({
        queryKey: ["restaurant", restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(`${apiContext.restaurantsUrl}/${restaurantId}`, true)
        )
    });


    if (isError) {
        return (
            <>
                <Error errorNumber={error.response.status}/>
            </>
        );
    } else if (isLoading) {
        return (
            <>
                <Page title={t("titles.loading")} className="restaurant">
                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="1920">
                        <rect x="288" y="50" rx="0" ry="0" width="1344" height="200"/>
                        <rect x="12" y="300" rx="0" ry="0" width="200" height="380"/>
                        <rect x="236" y="300" rx="0" ry="0" width="1268" height="64"/>
                        <rect x="236" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="656" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="1528" y="300" rx="0" ry="0" width="380" height="120"/>
                    </ContentLoader>
                </Page>
            </>
        );
    }
    return (
        <>
            <Page title={data.name} className="restaurant">
                <div className="header">
                    <img src={data.portrait1Url} alt={data.name}/>
                </div>
                <div className="d-flex justify-content-center">
                    <div className="information">
                        <img src={data.logoUrl} alt={data.name} className="logo"/>
                        <div className="flex-grow-1">
                            <h1>{data.name}</h1>
                            <p className="mb-1">
                                {data.description || <i>{t("restaurant.no_description")}</i>}
                            </p>
                            <p><i className="bi bi-geo-alt"></i> {data.address}</p>
                            {
                                data.reviewCount === 0
                                    ?
                                    <small className="text-muted">{t("restaurant.no_reviews")}</small>
                                    :
                                    <>
                                        <Rating rating={data.averageRating} count={data.reviewCount}/>
                                        <button className="btn btn-link" type="button">
                                            <small>{t("restaurant.view_reviews")}</small>
                                        </button>
                                    </>
                            }
                            <TagsContainer tags={data.tags} clickable={true}/>
                        </div>
                        <div className="d-flex flex-column gap-2">
                            <button className="btn btn-secondary" type="button">{t("restaurant.edit_menu")}</button>
                            <button className="btn btn-secondary" type="button">{t("restaurant.see_orders")}</button>
                            <button className="btn btn-danger" type="button">{t("restaurant.delete_restaurant")}</button>
                        </div>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default Restaurant;
