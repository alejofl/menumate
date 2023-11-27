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

function Home() {
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
                    size: 4
                }
            )
        )
    });

    return (
        <>
            <Page title={t("titles.home")}>
                <div className="landing-container">
                    <p className="landing-title">{t("home.call_to_action")}</p>
                    <form method="get" action="/restaurants">
                        <div className="search-form-container">
                            <div>
                                <div className="input-group flex-nowrap">
                                    <span className="input-group-text search-input"><i className="bi bi-search"></i></span>
                                    <input type="text" name="search" className="form-control search-input" placeholder={t("restaurants.search_placeholder")}/>
                                </div>
                            </div>
                        </div>
                        <input type="submit" className="btn btn-primary" value={t("restaurants.search_button")}/>
                    </form>
                </div>
                <div className="landing-restaurants">
                    <p className="landing-restaurants-title">{t("home.restaurants_title")}</p>
                    <div className="landing-restaurants-feed">
                        {
                            isLoading || isError ? "Loading..." : data.content.map(restaurant => {
                                return (
                                    <RestaurantCard
                                        key={restaurant.selfUrl}
                                        restaurantId={1}
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
