import {describe, it, expect} from "vitest";
import {useUserService} from "../../hooks/services/useUserService.js";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";

describe("useUserService", () => {
    describe("registerAddress", () => {
        it("should return 201 Created", async () => {
            const userService = useUserService(Api);
            const response = await userService.registerAddress(apiUrl("/users/1/addresses"));
            // eslint-disable-next-line no-magic-numbers
            expect(response.status).toBe(201);
        });
    });
});
