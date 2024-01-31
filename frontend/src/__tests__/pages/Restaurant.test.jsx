import {describe, it, expect, vi} from "vitest";
import {render, fireEvent, screen, waitForElementToBeRemoved, waitFor} from "../setup/react.jsx";
import Restaurant from "../../pages/Restaurant.jsx";

describe("Restaurant", () => {
    vi.mock("react-router-dom", async (importOriginal) => {
        const original = await importOriginal();
        return {
            ...original,
            useParams: vi.fn().mockReturnValue({restaurantId: 17})
        };
    });

    it("should open the Report Restaurant modal when the toast is clicked", async () => {
        const {container} = render(<Restaurant edit={false}/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const toastButton = container.querySelector("a[data-bs-target=\"#restaurant-report-modal\"]");
        fireEvent.click(toastButton);
        await waitFor(() => expect(container.querySelector("#restaurant-report-modal").classList).toContain("show"));
    });

    it("should disable the Place Order button on load", async () => {
        const {container} = render(<Restaurant edit={false}/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        expect(container.querySelector("#place-order-button").disabled).toBe(true);
    });

    it("should show if the user is placing an order to dine in or delivery", async () => {
        render(<Restaurant edit={false}/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        expect(document.querySelector(".restaurant_location_toast #text-toast > div > div > span").innerHTML).toMatch(/^You are ordering delivery or take-away/i);
    });

    it("should make the price operation correctly and show it in the Place Order button", async () => {
        const {container} = render(<Restaurant edit={false}/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        // eslint-disable-next-line no-magic-numbers
        const EXPECTED_PRICE = (9.99 * 2 + 11.99 * 3).toFixed(2);
        fireEvent.change(
            container.querySelector("#add-1-to-cart > div > div > div.modal-body > div > div > input[type='number']"),
            {target: {value: 2}}
        );
        fireEvent.click(container.querySelector("#add-1-to-cart > div > div > div.modal-footer > button.btn.btn-primary"));
        fireEvent.change(
            container.querySelector("#add-2-to-cart > div > div > div.modal-body > div > div > input[type='number']"),
            {target: {value: 3}}
        );
        fireEvent.click(container.querySelector("#add-2-to-cart > div > div > div.modal-footer > button.btn.btn-primary"));
        fireEvent.click(container.querySelector("#place-order-button"));

        expect(container.querySelector("div.place_order_modal > div > div > div > form > div.modal-footer > button").innerHTML).toContain(EXPECTED_PRICE);
    });
});
