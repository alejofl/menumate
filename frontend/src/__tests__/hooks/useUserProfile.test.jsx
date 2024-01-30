/* eslint-disable no-magic-numbers */
import {describe, it, expect} from "vitest";
import {useUserService} from "../../hooks/services/useUserService.js";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";
import Address from "../../data/model/Address.js";

describe("useUserService", () => {
    describe("registerAddress", () => {
        it("should return 201 Created", async () => {
            const userService = useUserService(Api);
            const response = await userService.registerAddress(apiUrl("/users/1/addresses"));
            expect(response.status).toBe(201);
        });
    });

    describe("getAddresses", () => {
        it("should return an array", async () => {
            const userService = useUserService(Api);
            const response = await userService.getAddresses(apiUrl("/users/1/addresses"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of Address", async () => {
            const userService = useUserService(Api);
            const response = await userService.getAddresses(apiUrl("/users/1/addresses"));
            response.forEach((item) => expect(item).toBeInstanceOf(Address));
        });

        it("should return correct data", async () => {
            const userService = useUserService(Api);
            const response = await userService.getAddresses(apiUrl("/users/1/addresses"));
            expect(response[0].address).toEqual(expect.any(String));
            expect(response[0].lastUsed).toEqual(expect.any(Date));
            expect(response[0].name).toEqual(expect.any(String));
            expect(response[0].selfUrl).toEqual(expect.any(String));
        });
    });

    describe("updateAddress", () => {
        it("should return 204 No Content", async () => {
            const userService = useUserService(Api);
            const response = await userService.updateAddress(apiUrl("/users/1/addresses/1"));
            expect(response.status).toBe(204);
        });
    });

    describe("deleteAddress", () => {
        it("should return 204 No Content", async () => {
            const userService = useUserService(Api);
            const response = await userService.deleteAddress(apiUrl("/users/1/addresses/1"));
            expect(response.status).toBe(204);
        });
    });
});
