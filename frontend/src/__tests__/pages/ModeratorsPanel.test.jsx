import {describe, it, expect} from "vitest";
import {render, fireEvent, screen, waitForElementToBeRemoved, waitFor} from "../setup/react.jsx";
import ModeratorsPanel from "../../pages/ModeratorsPanel.jsx";

describe("ModeratorsPanel", () => {
    it("should open the Edit Moderators modal when the button is clicked", async () => {
        const {container} = render(<ModeratorsPanel/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));
        await waitFor(() => container.querySelector("#edit-moderators-modal") !== null);

        const button = container.querySelector("button[data-bs-target=\".moderators_panel #edit-moderators-modal\"]");
        fireEvent.click(button);
        await waitFor(() => expect(container.querySelector("#edit-moderators-modal").classList).toContain("show"));
    });

    it("should open the Reports modal when the card is clicked", async () => {
        const {container} = render(<ModeratorsPanel/>);
        await waitForElementToBeRemoved(() => screen.getAllByText(/loading/i));

        const card = container.querySelector("main > div.restaurant-feed > div > div");
        fireEvent.click(card);
        await waitFor(() => expect(container.querySelector("main > div.reports_modal > div").classList).toContain("show"));
    });
});
