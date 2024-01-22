import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import RestaurantCard from "../components/RestaurantCard.jsx";
import { Link } from "react-router-dom";
import "./styles/home.styles.css";
import { useApi } from "../hooks/useApi.js";
import {useContext} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import { useRestaurantService } from "../hooks/services/useRestaurantService.js";
import { useQuery } from "@tanstack/react-query";
import ContentLoader from "react-content-loader";

function Home() {
    const MOST_RATED_RESTAURANTS_COUNT = 4;
    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const restaurantService = useRestaurantService(api);

    const { isLoading, isError, data } = useQuery({
        queryKey: ["restaurants", "home"],
        queryFn: async () => (
            await restaurantService.getRestaurants(
                apiContext.restaurantsUrl,
                {
                    orderBy: "rating",
                    descending: true,
                    size: MOST_RATED_RESTAURANTS_COUNT
                }
            )
        )
    });

    return (
        <>
            <Page title={t("titles.home")} className="home">
                <div className="landing-container">
                    <p className="landing-title">{t("home.call_to_action")}</p>
                    <form method="get" action="/restaurants">
                        <div className="search-form-container">
                            <div>
                                <div className="input-group flex-nowrap">
                                    <span className="input-group-text search-input"><i className="bi bi-search default"></i></span>
                                    <input type="text" name="search" className="form-control search-input" placeholder={t("restaurants.search_placeholder")}/>
                                </div>
                            </div>
                        </div>
                        <input type="submit" className="btn btn-primary" value={t("restaurants.search_button")}/>
                    </form>
                </div>
                <div className="landing-restaurants">
                    <p className="page-title">{t("home.restaurants_title")}</p>
                    <div className="landing-restaurants-feed">
                        {
                            isLoading || isError
                                ?
                                new Array(MOST_RATED_RESTAURANTS_COUNT).fill("").map((_, i) => {
                                    return (
                                        <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="18rem" height="18rem" key={i}>
                                            <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
                                        </ContentLoader>
                                    );
                                })
                                :
                                data.content.map(restaurant => {
                                    return (
                                        <RestaurantCard
                                            key={restaurant.selfUrl}
                                            restaurantId={restaurant.restaurantId}
                                            name={restaurant.name}
                                            mainImage={restaurant.portrait1Url}
                                            hoverImage={restaurant.portrait2Url}
                                            address={restaurant.address}
                                            rating={restaurant.averageRating}
                                            ratingCount={restaurant.reviewCount}
                                            tags={restaurant.tags}
                                        />
                                    );
                                })
                        }
                    </div>
                    <Link to="/restaurants" className="btn btn-primary">{t("home.restaurants_button")}</Link>
                </div>
            </Page>
        </>
    );
}

export default Home;
