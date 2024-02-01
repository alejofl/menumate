import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useParams, useSearchParams} from "react-router-dom";
import {useInfiniteQuery, useQuery, useQueryClient} from "@tanstack/react-query";
import "./styles/restaurant_orders.styles.css";
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

    const { t, i18n } = useTranslation();
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

    const [showInternalOrderModal, setShowInternalOrderModal] = useState(false);
    const [orderUrl, setOrderUrl] = useState("");
    const [error, setError] = useState(null);

    const queryClient = useQueryClient();
    const { restaurantId } = useParams();

    const {
        data: restaurant,
        isError: restaurantIsError
    } = useQuery({
        queryKey: ["restaurant", restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(apiContext.restaurantsUriTemplate.fill({restaurantId: restaurantId}), true)
        )
    });
    const {
        isError: userIsError,
        data: user,
        error: userError
    } = useQuery({
        queryKey: ["user", authContext.selfUrl],
        queryFn: async () => (
            await userService.getUser(authContext.selfUrl)
        )
    });
    const {
        isPending: userRoleIsPending,
        isError: userRoleIsError,
        data: userRole,
        error: userRoleError
    } = useQuery({
        queryKey: ["restaurant", restaurantId, "employees", user?.userId],
        queryFn: async () => (
            await userService.getRoleForRestaurant(
                restaurant.employeesUriTemplate.fill({userId: user.userId})
            )
        ),
        enabled: !!user && !!restaurant
    });
    const {
        isPending: ordersIsPending,
        data: orders,
        isFetchingNextPage: ordersIsFetchingNextPage,
        hasNextPage: ordersHasNextPage,
        fetchNextPage,
        isError: ordersIsError
    } = useInfiniteQuery( {
        queryKey: ["orders", status, sorting, query],
        queryFn: async ({ pageParam }) => (
            await orderService.getOrders(
                apiContext.ordersUrl,
                {
                    restaurantId: restaurant.restaurantId,
                    status: status,
                    descending: sorting === SORT.DESCENDING,
                    ...query,
                    page : pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: !!restaurant
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    const handleOnShowInternalOrderModal = (orderUrl) => {
        setOrderUrl(orderUrl);
        setShowInternalOrderModal(true);
    };
    const handleOnCloseInternalOrderModal = () => {
        queryClient.fetchInfiniteQuery({queryKey: ["orders", status, sorting, query]});
        setOrderUrl("");
        setShowInternalOrderModal(false);
    };

    if (restaurantIsError) {
        return (
            <>
                <Error errorNumber={restaurantIsError.response.status}/>
            </>
        );
    } else if (ordersIsError) {
        return (
            <>
                <Error errorNumber={ordersIsError.response.status}/>
            </>
        );
    } else if (userIsError) {
        return (
            <>
                <Error errorNumber={userError.response.status}/>
            </>
        );
    } else if (userRoleIsError) {
        return (
            <>
                <Error errorNumber={userRoleError.response.status}/>
            </>
        );
    } else if (!userRoleIsPending && !userRole.isOrderHandler) {
        return (
            <>
                <Error errorNumber={403}/>
            </>
        );
    } else if (error) {
        return (
            <>
                <Error errorNumber={error}/>
            </>
        );
    }
    return (
        <>
            <Page title={t("titles.restaurant_orders", {name: restaurant?.name})} className="restaurant_orders">
                <div className="page-title">
                    <h1>{t("restaurant_orders.title", {restaurantName: restaurant?.name})}</h1>
                </div>
                <div>
                    <nav>
                        <ul className="nav nav-pills nav-fill mb-4 secondary-nav">
                            <li className="nav-item">
                                <button
                                    className={"nav-link" + (status === STATUS.PENDING ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.PENDING)}
                                >
                                    {t("restaurant_orders.pending")}
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={"nav-link" + (status === STATUS.CONFIRMED ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.CONFIRMED)}
                                >
                                    {t("restaurant_orders.confirmed")}
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={"nav-link" + (status === STATUS.READY ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.READY)}>
                                    {t("restaurant_orders.ready")}
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={"nav-link" + (status === STATUS.DELIVERED ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.DELIVERED)}
                                >
                                    {t("restaurant_orders.delivered")}
                                </button>
                            </li>
                            <li className="nav-item">
                                <button
                                    className={"nav-link" + (status === STATUS.CANCELLED ? " active" : "")}
                                    aria-current="page"
                                    onClick={() => setStatus(STATUS.CANCELLED)}
                                >
                                    {t("restaurant_orders.cancelled")}
                                </button>
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
                                    <span className="clickable-object px-2">
                                        {sorting === SORT.ASCENDING
                                            ?
                                            <i className="bi bi-arrow-down-square-fill" onClick={() => setSorting(SORT.DESCENDING)}></i>
                                            :
                                            <i className="bi bi-arrow-up-square-fill" onClick={() => setSorting(SORT.ASCENDING)}></i>
                                        }
                                    </span>
                                </th>
                            </tr>
                        </thead>
                        <tbody className="table-striped justify-center">
                            {ordersIsPending
                                ?
                                new Array(ROWS_SKELETON).fill("").map((_, i) => (
                                    <tr key={i}>
                                        {
                                            new Array(ROWS_SKELETON).fill("").map((_, j) => (
                                                <th className="text-start" key={j}>
                                                    <ContentLoader backgroundColor="#eaeaea"
                                                        foregroundColor="#e0e0e0"
                                                        width="100%" height="20">
                                                        <rect x="0%" y="0" rx="0" ry="0" width="100%"
                                                            height="100"/>
                                                    </ContentLoader>
                                                </th>
                                            ))
                                        }
                                    </tr>
                                ))
                                :
                                orders?.pages[0]?.content.length !== 0 &&
                                orders?.pages.flatMap(page => page.content).map(order => (
                                    <tr
                                        className="clickable-object"
                                        key={order.orderId}
                                        onClick={() => handleOnShowInternalOrderModal(order.selfUrl)}
                                    >
                                        <td className="text-start">{order.orderId}</td>
                                        <td className="text-center">{t(`order.${order.orderType}`)}</td>
                                        <td className="text-center">{order.tableNumber || "-"}</td>
                                        <td className="text-center">{order.address || "-"}</td>
                                        <td className="text-end">
                                            {order.dateOrdered.toLocaleString(i18n.language)}
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
                        orders?.pages[0]?.content.length === 0 &&
                            <div className="empty-results">
                                <h1><i className="bi bi-slash-circle default"></i></h1>
                                <p>{t("restaurant_orders.table.empty_results")}</p>
                            </div>
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
                                            <span className="spinner-border spinner-border-sm mx-4" role="status"></span>
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
                        onError={(e) => setError(e)}
                    </InternalOrderModal>
                }
            </Page>
        </>
    )
    ;
}

export default RestaurantOrders;
