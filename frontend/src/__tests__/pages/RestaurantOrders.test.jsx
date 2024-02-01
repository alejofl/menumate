import {describe, it, expect, vi} from "vitest";
import {render, fireEvent, screen, waitForElementToBeRemoved} from "../setup/react.jsx";
import RestaurantOrders from "../../pages/RestaurantOrders.jsx";
import AuthContext from "../../contexts/AuthContext.jsx";
import {apiUrl} from "../setup/utils.js";
import InternalOrderModal from "../../components/InternalOrderModal.jsx";

describe("OrderRestaurants", () => {
    it("should open the Order modal when the row is clicked", async () => {
        vi.mock("react-router-dom", async (importOriginal) => {
            const original = await importOriginal();
            return {
                ...original,
                useParams: vi.fn().mockReturnValue({restaurantId: 17})
            };
        });
        const {container} = render(<AuthContext.Provider value={{
            isAuthenticated: true,
            jwt: "fadsfasdfdasfadsfasdfadsfasdf",
            refreshToken: "fadsfdasfadsfasdfdasf",
            name: "name",
            role: "role",
            isModerator: true,
            selfUrl: apiUrl("/users/1"),
            login: null,
            logout: null,
            updateTokens: null
        }}><RestaurantOrders>
                <InternalOrderModal
                    orderUrl="exampleOrderUrl"
                    showActions={true}
                    onClose={() => true}
                    onError={() => false}
                />
            </RestaurantOrders></AuthContext.Provider>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));
        const clickableRow = container.querySelector("table tbody tr");
        fireEvent.click(clickableRow);
        console.log("SIIIIII", clickableRow.classList.toString());
        screen.debug();
        expect(screen.queryByText("Order Details")).not.toBeNull();
        /*
         * await waitFor(() => console.log(container.querySelector("#internal-order-modal").classList.toString()));
         * await waitFor(() => expect(container.querySelector("#internal-order-modal").classList.toString()).toEqual(expect.stringContaining("show")));
         * await waitFor(() =>
         *     expect(container.querySelector("#internal-order-modal").classList.toString()).toEqual(expect.stringContaining("show"))
         * );
         */
    });
});
