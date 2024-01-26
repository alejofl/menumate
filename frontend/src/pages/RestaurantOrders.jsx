import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useParams, useSearchParams} from "react-router-dom";
import {useInfiniteQuery, useQueries, useQuery} from "@tanstack/react-query";
import "./styles/restaurant_orders.css";
import {STATUS} from "../utils.js";
import {useOrderService} from "../hooks/services/useOrderService.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";
import ContentLoader from "react-content-loader";

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

    const [currentOrder, setCurrentOrder] = useState("");
    const [currentOrderId, setCurrentOrderId] = useState();

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

    const {
        isLoading: ordersIsLoading,
        data: orders,
        isFetchingNextPage: ordersIsFetchingNextPage,
        hasNextPage: ordersHasNextPage,
        fetchNextPage
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
        enabled: restaurantIsSuccess && userIsSuccess
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };
    console.log(apiContext.ordersUrl);

    const {
        data: order
    } = useQuery({
        queryKey: ["order", currentOrder, currentOrderId],
        queryFn: async () => (
            await orderService.getOrder(
                currentOrder
            )
        )
    });

    const {
        data: orderItems,
        isSuccess: isSuccessOrderItems
    } = useQuery({
        queryKey: ["orderItems", order],
        queryFn: async () => (
            await orderService.getOrderItems(
                order.itemsUrl
            )
        ),
        enabled: !!order
    });

    console.log(orderItems);

    const products = useQueries({
        queries: isSuccessOrderItems
            ?
            orderItems.map(orderItem => {
                return {
                    queryKey: ["order", orderItem.productUrl],
                    queryFn: async () => (
                        await restaurantService.getProduct(orderItem.productUrl)
                    )
                };
            })
            :
            []
    });
    console.log(products);
    const handleOrder = () => {
        // eslint-disable-next-line no-undef
        const modal = bootstrap.Modal.getOrCreateInstance(document.querySelector("#order-details"));
        // document.querySelector("#editAddressModal").addEventListener("hidden.bs.modal", handleModalHidden);
        modal.show();
    };

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
                                                    onClick={() => { handleOrder(); setCurrentOrder(order.selfUrl); setCurrentOrderId(order.orderId); }}>
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

                <div className="modal fade" id="order-details" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-scrollable modal-lg modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h5 className="modal-title">{t("order.title", {id: order?.orderId})}</h5>
                                <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div className="modal-body">
                                <h4>{t("order.items")}</h4>
                                <table className="table table-hover">
                                    <thead className="table-light">
                                        <tr>
                                            <th scope="col" className="text-start">#</th>
                                            <th scope="col" className="text-center">{t("order.product")}</th>
                                            <th scope="col" className="text-center">{t("order.comments")}</th>
                                            <th scope="col" className="text-center">{t("order.quantity")}</th>
                                            <th scope="col" className="text-end">{t("order.price")}</th>
                                        </tr>
                                    </thead>
                                    <tbody id="order-items">
                                        {
                                            isSuccessOrderItems && products.every(query => query.isSuccess) &&
                                            orderItems.map(orderItem => (
                                                <tr key={orderItem}>
                                                    <th scope="col" className="text-start">{orderItem.lineNumber}</th>
                                                    <th scope="col" className="text-center">
                                                        {products.find(product =>
                                                            product.data.selfUrl === orderItem.productUrl
                                                        )?.data.name}
                                                    </th>
                                                    <th scope="col" className="text-center">{orderItem.comment}</th>
                                                    <th scope="col" className="text-center">{orderItem.quantity}</th>
                                                    <th scope="col" className="text-end">
                                                        {products.find(product =>
                                                            product.data.selfUrl === orderItem.productUrl
                                                        ).data.price}
                                                    </th>
                                                </tr>
                                            ))
                                        }
                                    </tbody>
                                </table>
                                <h4>{t("order.details")}</h4>
                                {/* <ul className="list-group list-group-flush">*/}
                                {/*    <li className="list-group-item d-flex align-items-center">*/}
                                {/*        <i className="bi bi-person me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted">{t("order.customer")}</small>*/}
                                {/*            <p className="mb-0" id="order-details-customer">*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/*    <li className="list-group-item d-flex align-items-center order-details-0-data">*/}
                                {/*        <i className="bi bi-card-list me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted">{t("order.order_type")}</small>*/}
                                {/*            <p className="mb-0">*/}
                                {/*                <spring:message code="restaurant.menu.form.dinein"/>*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/*    <li className="list-group-item d-flex align-items-center order-details-0-data">*/}
                                {/*        <i className="bi bi-hash me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted">{t("order.table_number")}</small>*/}
                                {/*            <p className="mb-0" id="order-details-table-number">*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/*    <li className="list-group-item d-flex align-items-center order-details-1-data">*/}
                                {/*        <i className="bi bi-card-list me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted"><spring:message code="userorders.ordertype"/></small>*/}
                                {/*            <p className="mb-0">*/}
                                {/*                <spring:message code="restaurant.menu.form.takeaway"/>*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/*    <li className="list-group-item d-flex align-items-center order-details-2-data">*/}
                                {/*        <i className="bi bi-card-list me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted"><spring:message code="userorders.ordertype"/></small>*/}
                                {/*            <p className="mb-0">*/}
                                {/*                <spring:message code="restaurant.menu.form.delivery"/>*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/*    <li className="list-group-item d-flex align-items-center order-details-2-data">*/}
                                {/*        <i className="bi bi-geo-alt me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted"><spring:message code="restaurant.menu.form.address"/></small>*/}
                                {/*            <p className="mb-0" id="order-details-address">*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/*    <li className="list-group-item d-flex align-items-center">*/}
                                {/*        <i className="bi bi-cash me-3"></i>*/}
                                {/*        <div>*/}
                                {/*            <small className="text-muted"><spring:message code="restaurantorders.modal.total"/></small>*/}
                                {/*            <p className="mb-0" id="order-total-price">*/}
                                {/*            </p>*/}
                                {/*        </div>*/}
                                {/*    </li>*/}
                                {/* </ul>*/}
                            </div>
                            {/* <c:if test="${status != 'delivered' && status != 'cancelled'}">*/}
                            {/*    <div class="modal-footer">*/}
                            {/*        <form action="<c:url value="/orders/$1/cancel"/>" method="post" id="cancel-order-form">*/}
                            {/*            <button type="submit" class="btn btn-danger"><spring:message code="restaurantorders.cancel"/></button>*/}
                            {/*        </form>*/}
                            {/*        <c:choose>*/}
                            {/*            <c:when test="${status == 'pending'}">*/}
                            {/*                <form action="<c:url value="/orders/$1/confirm"/>" method="post" id="change-order-status-form">*/}
                            {/*                    <button type="submit" class="btn btn-success"><spring:message code="restaurantorders.confirm"/></button>*/}
                            {/*                </form>*/}
                            {/*            </c:when>*/}
                            {/*            <c:when test="${status == 'confirmed'}">*/}
                            {/*                <form action="<c:url value="/orders/$1/ready"/>" method="post" id="change-order-status-form">*/}
                            {/*                    <button type="submit" class="btn btn-success"><spring:message code="restaurantorders.ready.action"/></button>*/}
                            {/*                </form>*/}
                            {/*            </c:when>*/}
                            {/*            <c:when test="${status == 'ready'}">*/}
                            {/*                <form action="<c:url value="/orders/$1/deliver"/>" method="post" id="change-order-status-form">*/}
                            {/*                    <button type="submit" class="btn btn-success"><spring:message code="restaurantorders.deliver"/></button>*/}
                            {/*                </form>*/}
                            {/*            </c:when>*/}
                            {/*        </c:choose>*/}
                            {/*    </div>*/}
                            {/* </c:if>*/}
                        </div>
                    </div>
                </div>
            </Page>
        </>
    )
    ;
}

export default RestaurantOrders;
