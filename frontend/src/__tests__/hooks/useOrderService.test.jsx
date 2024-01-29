import { describe, it, expect } from "vitest";
import {useOrderService} from "../../hooks/services/useOrderService.js";
import Order from "../../data/model/Order.js";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";

describe("useOrderService", () => {
    describe("getOrder", () => {
        it("should return an instance of Order class", async () => {
            const orderService = useOrderService(Api);
            const response = await orderService.getOrder(apiUrl("/orders/1"));
            expect(response instanceof Order).toBe(true);
        });
    });
});
