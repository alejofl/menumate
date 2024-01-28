import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import "./styles/user_orders.styles.css";
import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import {useContext, useState} from "react";
import AuthContext from "../contexts/AuthContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";
import {useApi} from "../hooks/useApi.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import {DEFAULT_RESTAURANT_COUNT} from "../utils.js";
import {useParams, useSearchParams} from "react-router-dom";
import {useOrderService} from "../hooks/services/useOrderService.js";
import OrderCard from "../components/OrderCard.jsx";
import ApiContext from "../contexts/ApiContext.jsx";

function UserOrders() {
    const { t } = useTranslation();
    const authContext = useContext(AuthContext);
    const apiContext = useContext(ApiContext);
    const api = useApi();
    const userService = useUserService(api);
    const orderService = useOrderService(api);

    const { orderId } = useParams();
    const [queryParams] = useSearchParams();
    const [inProgress, setInProgress] = useState(true);
    const [error, setError] = useState(null);

    const { isError: userIsError, data: user, error: userError} = useQuery({
        queryKey: ["user", authContext.selfUrl],
        queryFn: async () => (
            await userService.getUser(authContext.selfUrl)
        ),
        enabled: authContext.isAuthenticated
    });

    const {
        isPending: ordersIsPending,
        isError: ordersIsError,
        data: orders,
        error: ordersError,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["user", authContext.selfUrl, "orders", inProgress],
        queryFn: async ({ pageParam }) => (
            await orderService.getOrders(
                user.ordersUrl,
                {
                    descending: true,
                    inProgress: inProgress,
                    page: pageParam,
                    ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: !!user
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    if (userIsError) {
        return (
            <>
                <Error errorNumber={userError.response.status}/>
            </>
        );
    } else if (ordersIsError) {
        return (
            <>
                <Error errorNumber={ordersError.response.status}/>
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
            <Page title={t("titles.user_orders")} className="user_orders">
                <h1 className="page-title">{t("titles.user_orders")}</h1>
                <ul className="nav nav-pills nav-fill nav-justified secondary-nav justify-content-center mb-4">
                    <li className="nav-item">
                        <button className={`nav-link ${inProgress ? "active" : ""}`} type="button" onClick={() => setInProgress(true)}>
                            {t("order.in_progress")}
                        </button>
                    </li>
                    <li className="nav-item">
                        <button className={`nav-link ${inProgress ? "" : "active"}`} type="button" onClick={() => setInProgress(false)}>
                            {t("order.all")}
                        </button>
                    </li>
                </ul>
                <div className="orders-feed">
                    {
                        ordersIsPending
                            ?
                            new Array(DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
                                return (
                                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="15rem" height="20rem" key={i}>
                                        <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
                                    </ContentLoader>
                                );
                            })
                            :
                            (orders.pages[0]?.content.length || 0) === 0
                                ?
                                <div className="empty-results">
                                    <h1><i className="bi bi-slash-circle default"></i></h1>
                                    <p>{t("restaurants.no_results")}</p>
                                </div>
                                :
                                <>
                                    {
                                        orderId && <OrderCard orderUrl={`${apiContext.ordersUrl}/${orderId}`} showCard={false} onError={(e) => setError(e)}/>
                                    }
                                    {
                                        orders.pages.flatMap(page => page.content).map((order, i) => {
                                            return (
                                                <OrderCard orderUrl={order.selfUrl} key={i} onError={(e) => setError(e)}/>
                                            );
                                        })
                                    }
                                </>
                    }
                    {
                        isFetchingNextPage &&
                        new Array(queryParams.get("size") || DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
                            return (
                                <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="15rem" height="20rem" key={i}>
                                    <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
                                </ContentLoader>
                            );
                        })
                    }
                </div>
                {
                    hasNextPage &&
                    <div className="d-flex justify-content-center align-items-center pt-2 pb-3">
                        <button onClick={handleLoadMoreContent} className="btn btn-primary" disabled={isFetchingNextPage}>
                            {
                                isFetchingNextPage
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
            </Page>
        </>
    );
}

export default UserOrders;
