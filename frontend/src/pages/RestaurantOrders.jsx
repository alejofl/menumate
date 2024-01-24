import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useParams} from "react-router-dom";
import {useQuery} from "@tanstack/react-query";
import "./styles/restaurant_orders.css";
import {STATUS} from "../utils.js";

function RestaurantOrders() {
    const { t } = useTranslation();
    const apiContext = useContext(ApiContext);
    // const authContext = useContext(AuthContext);
    const api = useApi();
    // const userService = useUserService(api);
    const restaurantService = useRestaurantService(api);
    const [status, setStatus] = useState(STATUS.PENDING);

    const { restaurantId } = useParams();
    const {
        data: restaurant
    } = useQuery({
        queryKey: [restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(`${apiContext.restaurantsUrl}/${restaurantId}`, true)
        )
    });

    return (
        <>
            <Page title={t("titles.home")} className="restaurant_orders">
                <div className="page-title">
                    <h1>{t("restaurant_orders.title", {restaurantName: restaurant?.name})}</h1>
                </div>
                <div className="restaurant-orders-view">
                    <nav>
                        <ul className="nav nav-pills nav-fill mb-3">
                            <li className="nav-item">
                                <a className={"nav-link" + (status === STATUS.PENDING ? " active" : "")} aria-current="page"
                                    onClick={() => setStatus(STATUS.PENDING)}>
                                    {t("restaurant_orders.pending")}
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className={"nav-link" + (status === STATUS.CONFIRMED ? " active" : "")} aria-current="page"
                                    onClick={() => setStatus(STATUS.CONFIRMED)}>
                                    {t("restaurant_orders.confirmed")}
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className={"nav-link" + (status === STATUS.READY ? " active" : "")} aria-current="page"
                                    onClick={() => setStatus(STATUS.READY)}>
                                    {t("restaurant_orders.ready")}
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className={"nav-link" + (status === STATUS.DELIVERED ? " active" : "")} aria-current="page"
                                    onClick={() => setStatus(STATUS.DELIVERED)}>
                                    {t("restaurant_orders.delivered")}
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className={"nav-link" + (status === STATUS.CANCELLED ? " active" : "")} aria-current="page"
                                    onClick={() => setStatus(STATUS.CANCELLED)}>
                                    {t("restaurant_orders.cancelled")}
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>
            </Page>
        </>
    );
}

export default RestaurantOrders;
