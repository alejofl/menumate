import {describe, expect, it} from "vitest";
import {fireEvent, render, screen, waitForElementToBeRemoved} from "../setup/react.jsx";
import UserProfile from "../../pages/UserProfile.jsx";
import {apiUrl} from "../setup/utils.js";
import AuthContext from "../../contexts/AuthContext.jsx";
import {waitFor} from "@testing-library/react";

describe("UserProfile", () => {
    it("should open the address modal when the button is clicked", async () => {
        const {container} = render(<AuthContext.Provider value={{
            isAuthenticated: true,
            jwt: "jsonwebtoken",
            refreshToken: "token",
            name: "name",
            role: "role",
            isModerator: true,
            selfUrl: apiUrl("/users/1"),
            login: null,
            logout: null,
            updateTokens: null
        }}><UserProfile></UserProfile></AuthContext.Provider>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));
        const button = container.querySelector(".btn");
        fireEvent.click(button);
        waitFor(() => {
            expect(container.querySelector("#editAddressModal").classList).toContain("show");
        });
    });
});
