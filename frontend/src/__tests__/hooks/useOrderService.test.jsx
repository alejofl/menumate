/* eslint-disable no-magic-numbers */
import {describe, it, expect} from "vitest";
import {useOrderService} from "../../hooks/services/useOrderService.js";
import Order from "../../data/model/Order.js";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";
import OrderItem from "../../data/model/OrderItem.js";
import PagedContent from "../../data/model/PagedContent.js";

describe("useOrderService", () => {
    describe("placeOrder", () => {
        it("should return 201 Created", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.placeOrder(apiUrl("/orders"), {});
            expect(response.status).toBe(201);
        });
    });

    describe("getOrder", () => {
        it("should return an instance of Order", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrder(apiUrl("/orders/1"));
            expect(response).toBeInstanceOf(Order);
        });

        it("should return correct data", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrder(apiUrl("/orders/1"));
            expect(response.orderId).toEqual(expect.any(Number));
            expect(response.orderType).toEqual(expect.any(String));
            expect(response.restaurantUrl).toEqual(expect.any(String));
            expect(response.selfUrl).toEqual(expect.any(String));
            expect(response.status).toEqual(expect.any(String));
            expect(response.userUrl).toEqual(expect.any(String));
            expect(response.itemsUrl).toEqual(expect.any(String));
        });
    });

    describe("getOrderItems", () => {
        it("should return an array", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrderItems(apiUrl("/orders/1/items"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of OrderItem", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrderItems(apiUrl("/orders/1/items"));
            response.forEach((item) => expect(item).toBeInstanceOf(OrderItem));
        });

        it("should return correct data", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrderItems(apiUrl("/orders/1/items"));
            expect(response[0].lineNumber).toEqual(expect.any(Number));
            expect(response[0].productUrl).toEqual(expect.any(String));
            expect(response[0].quantity).toEqual(expect.any(Number));
        });
    });

    describe("getOrders", () => {
        it("should return an instance of PagedContent", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrders(apiUrl("/orders"));
            expect(response).toBeInstanceOf(PagedContent);
        });

        it("should return an array of Order as content", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrders(apiUrl("/orders"));
            response.content.forEach((item) => expect(item).toBeInstanceOf(Order));
        });

        it("should return correct data", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrders(apiUrl("/orders"));
            expect(response.content[0].orderId).toEqual(expect.any(Number));
            expect(response.content[0].orderType).toEqual(expect.any(String));
            expect(response.content[0].restaurantUrl).toEqual(expect.any(String));
            expect(response.content[0].selfUrl).toEqual(expect.any(String));
            expect(response.content[0].status).toEqual(expect.any(String));
            expect(response.content[0].userUrl).toEqual(expect.any(String));
            expect(response.content[0].itemsUrl).toEqual(expect.any(String));
        });
    });

    describe("updateStatus", () => {
        it("should return 204 No Content", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.updateStatus(apiUrl("/orders/1"), {});
            expect(response.status).toBe(204);
        });
    });
});
