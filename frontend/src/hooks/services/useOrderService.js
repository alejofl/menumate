import {ORDER_ITEMS_CONTENT_TYPE, ORDER_TYPE, ORDERS_CONTENT_TYPE} from "../../utils.js";
import Order from "../../data/model/Order.js";
import OrderItem from "../../data/model/OrderItem.js";
import {parseLinkHeader} from "@web3-storage/parse-link-header";
import PagedContent from "../../data/model/PagedContent.js";

export function useOrderService(api) {
    const placeOrder = async (url, restaurantId, name, email, tableNumber, address, orderType, cart, language) => {
        return await api.post(
            url,
            {
                restaurantId: restaurantId,
                name: name,
                email: email,
                ...(orderType === ORDER_TYPE.DINE_IN && {tableNumber: tableNumber}),
                ...(orderType === ORDER_TYPE.DELIVERY && {address: address}),
                orderType: orderType,
                cart: cart
            },
            {
                headers: {
                    "Content-Type": ORDERS_CONTENT_TYPE,
                    "Accept-Language": language
                }
            }
        );
    };

    const getOrder = async (url) => {
        const response = await api.get(
            url,
            {
                headers: {
                    "Accept": ORDERS_CONTENT_TYPE
                }
            }
        );
        return Order.fromJSON(response.data);
    };

    const getOrderItems = async (url) => {
        const response = await api.get(
            url,
            {
                headers: {
                    "Accept": ORDER_ITEMS_CONTENT_TYPE
                }
            }
        );
        return Array.isArray(response.data) ? response.data.map(data => OrderItem.fromJSON(data)) : [];
    };

    const getOrders = async (url, params) => {
        const response = await api.get(
            url,
            {
                params: params,
                headers: {
                    "Accept": ORDERS_CONTENT_TYPE
                }
            }
        );
        const links = parseLinkHeader(response.headers?.link, {});
        const orders = Array.isArray(response.data) ? response.data.map(data => Order.fromJSON(data)) : [];
        return new PagedContent(
            orders,
            links?.first,
            links?.prev,
            links?.next,
            links?.last
        );
    };

    const updateStatus = async (url, status) => {
        console.log(status);
        return await api.patch(
            url,
            status,
            {
                headers: {
                    "Content-Type": ORDERS_CONTENT_TYPE
                }
            }
        );
    };

    return {
        placeOrder,
        getOrder,
        getOrderItems,
        getOrders,
        updateStatus
    };
}
