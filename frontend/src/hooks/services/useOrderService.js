import {ORDER_TYPE, ORDERS_CONTENT_TYPE} from "../../utils.js";

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

    return {
        placeOrder
    };
}
