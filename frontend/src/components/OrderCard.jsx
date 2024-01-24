import "./styles/order_card.styles.css";
import {useTranslation} from "react-i18next";
import {useQueries, useQuery} from "@tanstack/react-query";
import {useOrderService} from "../hooks/services/useOrderService.js";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "../pages/Error.jsx";
import ContentLoader from "react-content-loader";
import {ORDER_STATUS, ORDER_TYPE, PRICE_DECIMAL_DIGITS} from "../utils.js";
import {useNavigate} from "react-router-dom";

function OrderCard({orderUrl}) {
    const { t, i18n } = useTranslation();
    const api = useApi();
    const orderService = useOrderService(api);
    const restaurantService = useRestaurantService(api);

    const navigate = useNavigate();

    const { isError: orderIsError, data: order} = useQuery({
        queryKey: ["order", orderUrl],
        queryFn: async () => (
            await orderService.getOrder(orderUrl)
        )
    });
    const { isPending: restaurantIsPending, isError: restaurantIsError, data: restaurant } = useQuery({
        queryKey: ["order", orderUrl, "restaurant"],
        queryFn: async () => (
            await restaurantService.getRestaurant(order.restaurantUrl, false)
        ),
        enabled: !!order
    });
    const { isPending: orderItemsIsPending, isError: orderItemsIsError, data: orderItems} = useQuery({
        queryKey: ["order", orderUrl, "items"],
        queryFn: async () => (
            await orderService.getOrderItems(order.itemsUrl)
        ),
        enabled: !!order
    });
    const products = useQueries({
        queries: orderItems
            ?
            orderItems.sort((a, b) => a.lineNumber - b.lineNumber).map(item => {
                return {
                    queryKey: ["order", orderUrl, "items", item.lineNumber, "product"],
                    queryFn: async () => (
                        await restaurantService.getProduct(item.productUrl)
                    )
                };
            })
            :
            []
    });

    if (orderIsError || restaurantIsError || orderItemsIsError || products.some(product => product.isError)) {
        return (
            <Error errorNumber={500}/>
        );
    } else if (restaurantIsPending || orderItemsIsPending || products.some(product => product.isPending)) {
        return (
            <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="15rem" height="20rem">
                <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
            </ContentLoader>
        );
    }
    return (
        <>
            <div className="order_card">
                <div className="clickable-object" data-bs-toggle="modal" data-bs-target={`#order-${order.orderId}-details`}>
                    <div className="card">
                        <div className="card-body d-flex align-items-center">
                            <img src={restaurant.logoUrl} alt={restaurant.name}/>
                            <div>
                                <small className="text-muted">
                                    {t("order.title", {id: order.orderId})}
                                </small>
                                <h5 className="card-title mb-0">
                                    {restaurant.name}
                                </h5>
                            </div>
                        </div>
                        <ul className="list-group list-group-flush">
                            <li className="list-group-item">
                                <i className="bi bi-card-list me-2"></i>
                                {t(`order.${order.orderType}`)}
                            </li>
                            <li className="list-group-item">
                                <i className="bi bi-calendar-event me-2"></i>
                                {order.dateOrdered.toLocaleString(i18n.language)}
                            </li>
                            <li className="list-group-item">
                                <i className="bi bi-cart me-2"></i>
                                {t("order.product_quantity", {count: orderItems.reduce((total, item) => total + item.quantity, 0)})}
                            </li>
                            <li className="list-group-item">
                                <i className="bi bi-cash-stack me-2"></i>
                                ${orderItems.reduce((total, item, i) => total + item.quantity * products[i].data.price, 0).toFixed(PRICE_DECIMAL_DIGITS)}
                            </li>
                        </ul>
                        <div className="card-footer">
                            {t(`order.status.order_${order.status}`)}
                        </div>
                    </div>
                </div>
            </div>

            <div className="modal fade order_details_modal" id={`order-${order.orderId}-details`} tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                    <div className="modal-content">
                        <img src={restaurant.portrait1Url} alt={restaurant.name}/>
                        <div className="order-information">
                            <div className="order-details-restaurant">
                                <img src={restaurant.logoUrl} alt={restaurant.name}/>
                                <div>
                                    <small className="text-muted">{t("order.title", {id: order.orderId})}</small>
                                    <h3 className="card-title mb-0">{restaurant.name}</h3>
                                    <hr/>
                                    <i className="bi bi-calendar-event me-2"></i>
                                    {order.dateOrdered.toLocaleString(i18n.language)}
                                </div>
                            </div>
                            <div className={`alert alert-${ORDER_STATUS[order.status.toUpperCase()].bgColor} d-flex flex-column align-items-center mb-0`} role="alert">
                                <div className="pb-2">
                                    {t(`order.status.order_${order.status}`)}
                                </div>
                                <div className="progress w-100" role="progressbar">
                                    <div className={`progress-bar bg-${ORDER_STATUS[order.status.toUpperCase()].color}`} style={{width: ORDER_STATUS[order.status.toUpperCase()].progress}}></div>
                                </div>
                            </div>
                        </div>
                        <div className="modal-body">
                            <div className="card-body">
                                <div>
                                    <h4>{t("order.details")}</h4>
                                    <table className="table">
                                        <thead className="table-light">
                                            <tr>
                                                <th scope="col" className="text-start">#</th>
                                                <th scope="col" className="text-start">{t("order.product")}</th>
                                                <th scope="col" className="text-center">{t("order.quantity")}</th>
                                                <th scope="col" className="text-end">{t("order.price")}</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {orderItems.sort((a, b) => a.lineNumber - b.lineNumber).map((item, i) => {
                                                return (
                                                    <tr key={i}>
                                                        <th className="text-start" scope="row">{item.lineNumber}</th>
                                                        <td className="text-start">{products[i].data.name}</td>
                                                        <td className="text-center">{item.quantity}</td>
                                                        <td className="text-end">${products[i].data.price}</td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                        <tfoot>
                                            <tr>
                                                <th colSpan="3">{t("order.total_price")}</th>
                                                <th className="text-end">${orderItems.reduce((total, item, i) => total + item.quantity * products[i].data.price, 0).toFixed(PRICE_DECIMAL_DIGITS)}</th>
                                            </tr>
                                        </tfoot>
                                    </table>
                                    <ul className="list-group list-group-flush">
                                        <li className="list-group-item d-flex align-items-center">
                                            <i className="bi bi-card-list me-3"></i>
                                            <div>
                                                <small className="text-muted">{t("order.order_type")}</small>
                                                <p className="mb-0">{t(`order.${order.orderType}`)}</p>
                                            </div>
                                        </li>
                                        {
                                            order.orderType === ORDER_TYPE.DINE_IN &&
                                            <li className="list-group-item d-flex align-items-center">
                                                <i className="bi bi-hash me-3"></i>
                                                <div>
                                                    <small className="text-muted">{t("order.table_number")}</small>
                                                    <p className="mb-0" id="order-details-table-number">{order.tableNumber}</p>
                                                </div>
                                            </li>
                                        }
                                        {
                                            order.orderType === ORDER_TYPE.DELIVERY &&
                                            <li className="list-group-item d-flex align-items-center">
                                                <i className="bi bi-geo-alt me-3"></i>
                                                <div>
                                                    <small className="text-muted">{t("order.address")}</small>
                                                    <p className="mb-0" id="order-details-address">{order.address}</p>
                                                </div>
                                            </li>
                                        }
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button className="btn btn-primary" onClick={() => navigate(`/restaurants/${restaurant.restaurantId}`)} data-bs-dismiss="modal">
                                {t("order.order_again")}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default OrderCard;
