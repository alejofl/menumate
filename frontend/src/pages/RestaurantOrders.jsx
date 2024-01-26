import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useParams, useSearchParams} from "react-router-dom";
import {useInfiniteQuery, useQuery, useQueryClient} from "@tanstack/react-query";
import "./styles/restaurant_orders.css";
import {STATUS} from "../utils.js";
import {useOrderService} from "../hooks/services/useOrderService.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";
import ContentLoader from "react-content-loader";
import Error from "./Error.jsx";
import InternalOrderModal from "../components/InternalOrderModal.jsx";

function RestaurantOrders() {
    const SORT = {
        DESCENDING : -1,
        ASCENDING : 1
    };
    const ROWS_SKELETON = 5;

    const { t } = useTranslation();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const api = useApi();

    const userService = useUserService(api);
    const restaurantService = useRestaurantService(api);
    const orderService = useOrderService(api);

    const [status, setStatus] = useState(STATUS.PENDING);
    const [sorting, setSorting] = useState(SORT.ASCENDING);
    const [queryParams] = useSearchParams();
    const [query] = useState({
        ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
    });
    const queryKey = useState(query);

    const [showInternalOrderModal, setShowInternalOrderModal] = useState(false);
    const [orderUrl, setOrderUrl] = useState("");

    const queryClient = useQueryClient();
    const { restaurantId } = useParams();

    const {
        data: restaurant,
        // eslint-disable-next-line no-unused-vars
        isSuccess : restaurantIsSuccess,
        isError: restaurantIsError
    } = useQuery({
        queryKey: [restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(`${apiContext.restaurantsUrl}/${restaurantId}`, true)
        )
    });

    const {
        data : user,
        // eslint-disable-next-line no-unused-vars
        isSuccess : userIsSuccess,
        isError: userIsError
    } = useQuery({
        queryKey: ["user"],
        queryFn: async () => (
            await userService.getUser(
                authContext.selfUrl
            )
        )
    });

    const {
        isLoading: ordersIsLoading,
        data: orders,
        isFetchingNextPage: ordersIsFetchingNextPage,
        hasNextPage: ordersHasNextPage,
        fetchNextPage,
        isError: ordersIsError
    } = useInfiniteQuery( {
        queryKey: ["orders", status, sorting, queryKey],
        queryFn: async ({ pageParam }) => (
            await orderService.getOrders(
                apiContext.ordersUrl,
                {
                    userId: user.userId,
                    restaurantId: restaurant.restaurantId,
                    status: status,
                    descending: sorting === SORT.DESCENDING,
                    query,
                    page : pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: !!user && !!restaurant
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    const handleOnShowInternalOrderModal = (orderUrl) => {
        setOrderUrl(orderUrl);
        setShowInternalOrderModal(true);
    };
    const handleOnCloseInternalOrderModal = () => {
        queryClient.fetchInfiniteQuery({queryKey: ["orders", status, sorting, queryKey]});
        setOrderUrl("");
        setShowInternalOrderModal(false);
    };

    if (restaurantIsError) {
        return (
            <>
                <Error errorNumber={restaurantIsError.response.status}/>
            </>
        );
    } else if (userIsError) {
        return (
            <>
                <Error errorNumber={userIsError.response.status}/>
            </>
        );
    } else if (ordersIsError) {
        return (
            <>
                <Error errorNumber={ordersIsError.response.status}/>
            </>
        );
    }

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
                                        {sorting === SORT.ASCENDING
                                            ?
                                            <i className="bi bi-arrow-down-square-fill" onClick={() => setSorting(SORT.DESCENDING)}></i>
                                            :
                                            <i className="bi bi-arrow-up-square-fill" onClick={() => setSorting(SORT.ASCENDING)}></i>
                                        }
                                    </a>
                                </th>
                            </tr>
                        </thead>
                        <tbody className="table-striped justify-center">
                            {
                                ordersIsLoading
                                    ?
                                    <tr>
                                        {
                                            new Array(ROWS_SKELETON).fill("").map((_, i) => {
                                                return (
                                                    <th className="text-start" key={i}>
                                                        <ContentLoader backgroundColor="#eaeaea"
                                                            foregroundColor="#e0e0e0"
                                                            width="100%" height="20">
                                                            <rect x="0%" y="0" rx="0" ry="0" width="100%"
                                                                height="100"/>
                                                        </ContentLoader>
                                                    </th>
                                                );
                                            })
                                        }
                                    </tr>
                                    :
                                    orders?.pages[0]?.content.length === 0
                                        ?
                                        <div></div>
                                        :
                                        orders?.pages.flatMap(page => page.content)
                                            .map(order => (
                                                <tr className="clickable-object" key={order.orderId}
                                                    onClick={() => handleOnShowInternalOrderModal(order.selfUrl)}>
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
                                                    <td className="text-center">
                                                        {order.tableNumber ? order.tableNumber : "-"}
                                                    </td>
                                                    <td className="text-center">
                                                        {order.address ? order.address : "-"}
                                                    </td>
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
                            {
                                ordersIsFetchingNextPage &&
                                <tr>
                                    {
                                        new Array(ROWS_SKELETON).fill("").map((_, i) => {
                                            return (
                                                <th className="text-start" key={i}>
                                                    <ContentLoader backgroundColor="#eaeaea"
                                                        foregroundColor="#e0e0e0"
                                                        width="100%" height="20">
                                                        <rect x="0%" y="0" rx="0" ry="0" width="100%"
                                                            height="100"/>
                                                    </ContentLoader>
                                                </th>
                                            );
                                        })
                                    }
                                </tr>
                            }
                        </tbody>
                    </table>
                    {
                        orders?.pages[0]?.content.length === 0
                            ?
                            <div className="empty-results">
                                <h1><i className="bi bi-slash-circle"></i></h1>
                                <p>{t("restaurant_orders.table.empty_results")}</p>
                            </div>
                            :
                            <div></div>
                    }
                    {
                        ordersHasNextPage &&
                        <div className="d-flex justify-content-center align-items-center pt-2 pb-3">
                            <button onClick={handleLoadMoreContent} className="btn btn-primary"
                                disabled={ordersIsFetchingNextPage}>
                                {
                                    ordersIsFetchingNextPage
                                        ?
                                        <>
                                            <span className="spinner-border spinner-border-sm mx-4"
                                                role="status"></span>
                                            <span className="visually-hidden">Loading...</span>
                                        </>
                                        :
                                        t("paging.load_more")
                                }
                            </button>
                        </div>
                    }
                </div>
                {
                    showInternalOrderModal &&
                    <InternalOrderModal
                        orderUrl={orderUrl}
                        showActions={status !== STATUS.DELIVERED && status !== STATUS.CANCELLED}
                        onClose={() => handleOnCloseInternalOrderModal()}>
                    </InternalOrderModal>
                }
            </Page>
        </>
    )
    ;
}

export default RestaurantOrders;
