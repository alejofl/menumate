import {useEffect} from "react";
import { useTranslation } from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import {useOrderService} from "../hooks/services/useOrderService.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useMutation, useQueries, useQuery} from "@tanstack/react-query";
import {useUserService} from "../hooks/services/useUserService.js";
import {ORDER_TYPE, PRICE_DECIMAL_DIGITS, STATUS} from "../utils.js";
import {Modal} from "bootstrap";

function InternalOrderModal({orderUrl, showActions, onClose, onError}) {
    const { t } = useTranslation();
    const api = useApi();
    const orderService = useOrderService(api);
    const userService = useUserService(api);
    const restaurantService = useRestaurantService(api);

    useEffect(() => {
        const modal = Modal.getOrCreateInstance(document.querySelector(".internal_order_modal .modal"));
        modal.show();
    }, []);
    useEffect(() => {
        document.querySelector(".internal_order_modal .modal").addEventListener("hide.bs.modal", () => onClose());
    }, [onClose]);

    const { isError: orderIsError, data: order, error: orderError} = useQuery({
        queryKey: ["order", orderUrl],
        queryFn: async () => (
            await orderService.getOrder(orderUrl)
        )
    });
    const { isPending: orderItemsIsPending, isError: orderItemsIsError, data: orderItems, error: orderItemsError} = useQuery({
        queryKey: ["order", orderUrl, "items"],
        queryFn: async () => (
            await orderService.getOrderItems(order.itemsUrl)
        ),
        enabled: !!order
    });
    const { isPending: userIsPending, isError: userIsError, data: user, error: userError} = useQuery({
        queryKey: ["order", orderUrl, "user"],
        queryFn: async () => (
            await userService.getUser(order.userUrl)
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

    const updateStatusMutation = useMutation({
        mutationFn: async ({ newStatus }) => (
            await orderService.updateStatus(
                order.selfUrl,
                {
                    status: newStatus
                }
            )
        )
    });
    const handleUpdateStatusMutation = (newStatus) => {
        updateStatusMutation.mutate(
            {
                newStatus: newStatus
            },
            {
                onSuccess: () => {
                    const modal = Modal.getOrCreateInstance(document.querySelector(".internal_order_modal .modal"));
                    modal.hide();
                }
            }
        );
    };

    if (orderIsError) {
        onError(orderError.response.status);
    } else if (orderItemsIsError) {
        onError(orderItemsError.response.status);
    } else if (userIsError) {
        onError(userError.response.status);
    } else if (products.some(product => product.isError)) {
        // eslint-disable-next-line no-magic-numbers
        onError(500);
    }
    return (
        <div className="internal_order_modal">
            <div className="modal fade" tabIndex="-1" id="internal-order-modal">
                <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-lg">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{orderItemsIsPending ? t("titles.loading") : t("order.title", {id: order.orderId})}</h1>
                        </div>
                        <div className="modal-body">
                            {
                                orderItemsIsPending || userIsPending || products.some(product => product.isPending)
                                    ?
                                    <>
                                        <div className="d-flex align-items-center">
                                            <div className="spinner-border" role="status">
                                                <span className="visually-hidden">Loading...</span>
                                            </div>
                                        </div>
                                    </>
                                    :
                                    <>
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
                                                {orderItems.sort((a, b) => a.lineNumber - b.lineNumber).map((item, i) => {
                                                    return (
                                                        <tr key={i}>
                                                            <td className="text-start">{item.lineNumber}</td>
                                                            <td className="text-center">{products[i].data.name}</td>
                                                            <td className="text-center">{item.comment}</td>
                                                            <td className="text-center">{item.quantity}</td>
                                                            <td className="text-end">${products[i].data.price}</td>
                                                        </tr>
                                                    );
                                                })}
                                            </tbody>
                                        </table>
                                        <h4>{t("order.details")}</h4>
                                        <ul className="list-group list-group-flush">
                                            <li className="list-group-item d-flex align-items-center">
                                                <i className="bi bi-person me-3"></i>
                                                <div>
                                                    <small className="text-muted">{t("order.customer")}</small>
                                                    <p className="mb-0">{user.name} <a href={`mailto:${user.email}`}>&lt;{user.email}&gt;</a></p>
                                                </div>
                                            </li>
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
                                            <li className="list-group-item d-flex align-items-center">
                                                <i className="bi bi-cash me-3"></i>
                                                <div>
                                                    <small className="text-muted">{t("order.total_price")}</small>
                                                    <p className="mb-0" id="order-total-price">
                                                        ${orderItems.reduce((total, item, i) => total + item.quantity * products[i].data.price, 0).toFixed(PRICE_DECIMAL_DIGITS)}
                                                    </p>
                                                </div>
                                            </li>
                                        </ul>
                                    </>
                            }
                        </div>
                        {
                            showActions && !orderItemsIsPending &&
                            <div className="modal-footer">
                                <button className="btn btn-danger" type="submit"
                                    onClick={() => handleUpdateStatusMutation(STATUS.CANCELLED)}>
                                    {t("restaurant_orders.order_detail_modal.cancel_order")}
                                </button>
                                {
                                    order.status === STATUS.PENDING &&
                                    <button className="btn btn-success" type="submit"
                                        onClick={() => handleUpdateStatusMutation(STATUS.CONFIRMED)}>
                                        {t("restaurant_orders.order_detail_modal.confirm_order")}
                                    </button>
                                }
                                {
                                    order.status === STATUS.CONFIRMED &&
                                    <button className="btn btn-success" type="submit"
                                        onClick={() => handleUpdateStatusMutation(STATUS.READY)}>
                                        {t("restaurant_orders.order_detail_modal.mark_as_ready")}
                                    </button>
                                }
                                {
                                    order.status === STATUS.READY &&
                                    <button className="btn btn-success" type="submit"
                                        onClick={() => handleUpdateStatusMutation(STATUS.DELIVERED)}>
                                        {t("restaurant_orders.order_detail_modal.mark_as_delivered")}
                                    </button>
                                }
                            </div>
                        }
                    </div>
                </div>
            </div>
        </div>
    );
}

export default InternalOrderModal;
