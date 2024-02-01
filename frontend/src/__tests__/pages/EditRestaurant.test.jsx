import {describe, it, expect, vi} from "vitest";
import {render, screen, waitForElementToBeRemoved} from "../setup/react.jsx";
import Restaurant from "../../pages/Restaurant.jsx";
import AuthContext from "../../contexts/AuthContext.jsx";
import {apiUrl, DUMMY_AUTH_TOKEN, DUMMY_REFRESH_TOKEN} from "../setup/utils.js";

describe("EditRestaurant", () => {
    vi.mock("react-router-dom", async (importOriginal) => {
        const original = await importOriginal();
        return {
            ...original,
            useParams: vi.fn().mockReturnValue({restaurantId: 2})
        };
    });

    it("should show a button to add category", async () => {
        render(
            <AuthContext.Provider
                value={{
                    isAuthenticated: true,
                    jwt: DUMMY_AUTH_TOKEN,
                    refreshToken: DUMMY_REFRESH_TOKEN,
                    name: "Alejo",
                    role: "USER",
                    isModerator: false,
                    selfUrl: apiUrl("/users/1"),
                    login: null,
                    logout: null,
                    updateTokens: null
                }}
            >
                <Restaurant edit={true}/>
            </AuthContext.Provider>
        );
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));
        expect(screen.getAllByText(/add category/i)).toBeDefined();
    });

    it("should show a button to add product", async () => {
        render(
            <AuthContext.Provider
                value={{
                    isAuthenticated: true,
                    jwt: DUMMY_AUTH_TOKEN,
                    refreshToken: DUMMY_REFRESH_TOKEN,
                    name: "Alejo",
                    role: "USER",
                    isModerator: false,
                    selfUrl: apiUrl("/users/1"),
                    login: null,
                    logout: null,
                    updateTokens: null
                }}
            >
                <Restaurant edit={true}/>
            </AuthContext.Provider>
        );
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));
        expect(screen.getAllByText(/add product/i)).toBeDefined();
    });
});
