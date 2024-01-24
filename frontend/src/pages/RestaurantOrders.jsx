import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useParams, useSearchParams} from "react-router-dom";
import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import "./styles/restaurant_orders.css";
import {STATUS} from "../utils.js";
import {useOrderService} from "../hooks/services/useOrderService.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";

function RestaurantOrders() {
    const { t } = useTranslation();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const api = useApi();
    const userService = useUserService(api);
    const restaurantService = useRestaurantService(api);
    const orderService = useOrderService(api);
    const [status, setStatus] = useState(STATUS.PENDING);
    const [ascending, setAscending] = useState(true);

    const [queryParams] = useSearchParams();
    const [query] = useState({
        ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
    });
    const queryKey = useState(query);

    const { restaurantId } = useParams();
    const {
        data: restaurant,
        isSuccess : restaurantIsSuccess
    } = useQuery({
        queryKey: [restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(`${apiContext.restaurantsUrl}/${restaurantId}`, true)
        )
    });

    /*
     * const {
     *     isPending : userIsPending,
     *     isError : userIsError,
     *     error : userError,
     *     data : user,
     *     isSuccess : userIsSuccess
     * }
     */
    const {
        data : user,
        isSuccess : userIsSuccess
    } = useQuery({
        queryKey: ["user"],
        queryFn: async () => (
            await userService.getUser(
                authContext.selfUrl
            )
        )
    });

    /*
     * const {
     *     isLoading : ordersIsLoading,
     *     isError : ordersIsError,
     *     data: orders,
     *     isFetchingNextPage,
     *     hasNextPage,
     *     fetchNextPage
     * }
     */
    const {
        isLoading: ordersIsLoading,
        data: orders
    } = useInfiniteQuery( {
        queryKey: ["orders", status, queryKey],
        queryFn: async ({ pageParam }) => (
            await orderService.getOrders(
                apiContext.ordersUrl,
                user.userId,
                restaurant.restaurantId,
                status,
                null,
                null,
                {
                    ...query,
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: restaurantIsSuccess && userIsSuccess
    });
    console.log(orders);
    orders?.pages.flatMap(page => page.content).map(order => console.log(order.orderId));

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
                                <a className={"nav-link" + (status === STATUS.CONFIRMED ? " active" : "")}
                                    aria-current="page"
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
                                <a className={"nav-link" + (status === STATUS.DELIVERED ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.DELIVERED)}>
                                    {t("restaurant_orders.delivered")}
                                </a>
                            </li>
                            <li className="nav-item">
                                <a className={"nav-link" + (status === STATUS.CANCELLED ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.CANCELLED)}>
                                    {t("restaurant_orders.cancelled")}
                                </a>
                            </li>
                        </ul>
                    </nav>
                </div>

                <div className="table-responsive w-75">
                    <table className="table table-hover restaurant-orders-table">
                        <thead className="table-light">
                            <tr>
                                <th className="text-start" scope="col">{t("restaurant_orders.table.order_id")}</th>
                                <th className="text-center" scope="col">{t("restaurant_orders.table.order_type")}</th>
                                <th className="text-center" scope="col">{t("restaurant_orders.table.table_number")}</th>
                                <th className="text-center" scope="col">{t("restaurant_orders.table.address")}</th>
                                <th className="text-end" scope="col">{t("restaurant_orders.table.order_date")}
                                    <a className="clickable-object px-2">
                                        {ascending
                                            ?
                                            <i className="bi bi-arrow-down-square-fill" onClick={() => setAscending(false)}></i>
                                            :
                                            <i className="bi bi-arrow-up-square-fill" onClick={() => setAscending(true)}></i>
                                        }
                                    </a>
                                </th>
                            </tr>
                        </thead>
                        <tbody className="table-striped">
                            {
                                ordersIsLoading
                                    ?
                                    <tr className="clickable-object">
                                        <td className="text-start">NO CARGO</td>
                                        <td className="text-center">NO CARGO</td>
                                        <td className="text-center">NO CARGO</td>
                                        <td className="text-center">NO CARGO</td>
                                        <td className="text-end">NO CARGO</td>
                                    </tr>
                                    :
                                    orders?.pages[0]?.content.length === 0
                                        ?
                                        <tr className="clickable-object">
                                            <td className="text-start">CARGO PERO NO DEL TODO</td>
                                            <td className="text-center">CARGO</td>
                                            <td className="text-center">CARGO</td>
                                            <td className="text-center">CARGO</td>
                                            <td className="text-end">CARGO</td>
                                        </tr>
                                        :
                                        orders?.pages.flatMap(page => page.content).map(order => (
                                            <tr className="clickable-object" key={order.orderId}>
                                                <td className="text-start">{order.orderId}</td>
                                                <td className="text-center">
                                                    {order.orderType === "delivery"
                                                        ?
                                                        t("order.delivery")
                                                        :
                                                        order.orderType === "takeaway"
                                                            ?
                                                            t("order.takeaway")
                                                            :
                                                            t("order.dinein")
                                                    }
                                                </td>
                                                <td className="text-center">{order.tableNumber ? order.tableNumber : "-"}</td>
                                                <td className="text-center">{order.address ? order.address : "-"}</td>
                                                <td className="text-end">
                                                    {STATUS.getStatusDescription(
                                                        status,
                                                        order.dateOrdered,
                                                        order.dateConfirmed,
                                                        order.dateReady,
                                                        order.dateDelivered,
                                                        order.dateCancelled
                                                    )}
                                                </td>
                                            </tr>
                                        ))

                            }
                        </tbody>
                    </table>
                </div>
            </Page>
        </>
    )
    ;
}

export default RestaurantOrders;
