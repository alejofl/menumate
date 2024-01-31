import {describe, it, expect, vi} from "vitest";
import {render, fireEvent, screen, waitForElementToBeRemoved, waitFor} from "../setup/react.jsx";
import Restaurant from "../../pages/Restaurant.jsx";

describe("Restaurant", () => {
    it("should open the Report Restaurant modal when the toast is clicked", async () => {
        vi.mock("react-router-dom", async (importOriginal) => {
            const original = await importOriginal();
            return {
                ...original,
                useParams: vi.fn().mockReturnValue({restaurantId: 17})
            };
        });
        const {container} = render(<Restaurant edit={false}/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const toastButton = container.querySelector("a[data-bs-target=\"#restaurant-report-modal\"]");
        fireEvent.click(toastButton);
        await waitFor(() => expect(container.querySelector("#restaurant-report-modal").classList).toContain("show"));
    });
});
