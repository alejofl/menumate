/* eslint-disable no-magic-numbers */
import {describe, it, expect} from "vitest";
import {useUserService} from "../../hooks/services/useUserService.js";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";
import Address from "../../data/model/Address.js";
import User from "../../data/model/User.js";
import UserRoleForRestaurant from "../../data/model/UserRoleForRestaurant.js";

describe("useUserService", () => {
    describe("sendResetPasswordToken", () => {
        it("should return 201 Created", async () => {
            const userService = useUserService(Api);
            const response = await userService.sendResetPasswordToken(apiUrl("/users/1"));
            expect(response.status).toBe(201);
        });
    });

    describe("resetPassword", () => {
        it("should return 204 No Content", async () => {
            const userService = useUserService(Api);
            const response = await userService.resetPassword(apiUrl("/users/1"));
            expect(response.status).toBe(204);
        });
    });

    /*
     * describe("login", () => {
     *     it("should return an array", async () => {
     *         const userService = useUserService(Api);
     *         const response = await userService.getAddresses(apiUrl("/users/1/addresses"));
     *         expect(Array.isArray(response)).toBe(true);
     *     });
     *
     *     it("should return an array of Address", async () => {
     *         const userService = useUserService(Api);
     *         const response = await userService.getAddresses(apiUrl("/users/1/addresses"));
     *         response.forEach((item) => expect(item).toBeInstanceOf(Address));
     *     });
     *
     *     it("should return correct data", async () => {
     *         const userService = useUserService(Api);
     *         const response = await userService.getAddresses(apiUrl("/users/1/addresses"));
     *         expect(response[0].address).toEqual(expect.any(String));
     *         expect(response[0].lastUsed).toEqual(expect.any(Date));
     *         expect(response[0].name).toEqual(expect.any(String));
     *         expect(response[0].selfUrl).toEqual(expect.any(String));
     *     });
     * });
     */

    describe("register", () => {
        it("should return 201 Created", async () => {
            const userService = useUserService(Api);
            const response = await userService.register(apiUrl("/users"));
            expect(response.status).toBe(201);
        });
    });

    describe("registerAddress", () => {
        it("should return 201 Created", async () => {
            const userService = useUserService(Api);
            const response = await userService.registerAddress(apiUrl("/users/1/addresses"));
            expect(response.status).toBe(201);
        });
    });

    describe("getUser", () => {
        it("should return an object User", async () => {
            const userService = useUserService(Api);
            const response = await userService.getUser(apiUrl("/users/1"));
            expect(response).toBeInstanceOf(User);
        });

        it("should return correct data", async () => {
            const userService = useUserService(Api);
            const response = await userService.getUser(apiUrl("/users/1"));
            expect(response.addressesUrl).toEqual(expect.any(String));
            expect(response.dateJoined).toEqual(expect.any(Date));
            expect(response.email).toEqual(expect.any(String));
            expect(response.isActive).toEqual(expect.any(Boolean));
            expect(response.name).toEqual(expect.any(String));
            expect(response.ordersUrl).toEqual(expect.any(String));
            expect(response.preferredLanguage).toEqual(expect.any(String));
            expect(response.restaurantsEmployedAtUrl).toEqual(expect.any(String));
            expect(response.reviewsUrl).toEqual(expect.any(String));
            expect(response.role).toEqual(expect.any(String));
            expect(response.selfUrl).toEqual(expect.any(String));
            expect(response.userId).toEqual(expect.any(Number));
        });
    });

    describe("getUsers", () => {
        it("should return an array", async () => {
            const userService = useUserService(Api);
            const response = await userService.getUsers(apiUrl("/users"));
            expect(Array.isArray(response)).toBe(true);
        });

        it("should return an array of User", async () => {
            const userService = useUserService(Api);
            const response = await userService.getUsers(apiUrl("/users"));
            response.forEach((item) => expect(item).toBeInstanceOf(User));
        });

        it("should return correct data", async () => {
            const userService = useUserService(Api);
            const response = await userService.getUsers(apiUrl("/users"));
            expect(response[0].addressesUrl).toEqual(expect.any(String));
            expect(response[0].dateJoined).toEqual(expect.any(Date));
            expect(response[0].email).toEqual(expect.any(String));
            expect(response[0].isActive).toEqual(expect.any(Boolean));
            expect(response[0].name).toEqual(expect.any(String));
            expect(response[0].ordersUrl).toEqual(expect.any(String));
            expect(response[0].preferredLanguage).toEqual(expect.any(String));
            expect(response[0].restaurantsEmployedAtUrl).toEqual(expect.any(String));
            expect(response[0].reviewsUrl).toEqual(expect.any(String));
            expect(response[0].role).toEqual(expect.any(String));
            expect(response[0].selfUrl).toEqual(expect.any(String));
            expect(response[0].userId).toEqual(expect.any(Number));
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

    describe("getRoleForRestaurant", () => {
        it("should return a UserRoleForRestaurant", async () => {
            const userService = useUserService(Api);
            const response = await userService.getRoleForRestaurant(apiUrl("restaurants/1/employees/1"));
            expect(response).toBeInstanceOf(UserRoleForRestaurant);
        });

        it("should return correct data", async () => {
            const userService = useUserService(Api);
            const response = await userService.getRoleForRestaurant(apiUrl("restaurants/1/employees/1"));
            expect(response.email).toEqual(expect.any(String));
            expect(response.name).toEqual(expect.any(String));
            expect(response.restaurantUrl).toEqual(expect.any(String));
            expect(response.role).toEqual(expect.any(String));
            expect(response.selfUrl).toEqual(expect.any(String));
            expect(response.userUrl).toEqual(expect.any(String));
        });
    });

    describe("addModerator", () => {
        it("should return 201 Created", async () => {
            const userService = useUserService(Api);
            const response = await userService.addModerator(apiUrl("restaurants/1/employees"));
            expect(response.status).toBe(201);
        });
    });

    describe("deleteModerator", () => {
        it("should return 204 No Content", async () => {
            const userService = useUserService(Api);
            const response = await userService.deleteModerator(apiUrl("restaurants/1/employees/1"));
            expect(response.status).toBe(204);
        });
    });
});
