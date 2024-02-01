import {describe, expect, it, vi} from "vitest";
import {render, screen, waitForElementToBeRemoved, waitFor} from "../setup/react.jsx";
import UserOrders from "../../pages/UserOrders.jsx";

describe("UserOrders", () => {
    it("should open modal when orderId is received as param", async () => {
        vi.mock("react-router-dom", async (importOriginal) => {
            const original = await importOriginal();
            return {
                ...original,
                useParams: vi.fn().mockReturnValue({orderId: 17})
            };
        });
        const {container} = render(<UserOrders></UserOrders>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));
        await waitFor(() => expect(container.querySelector("#order-17-details")).toBeDefined());
    });
});
