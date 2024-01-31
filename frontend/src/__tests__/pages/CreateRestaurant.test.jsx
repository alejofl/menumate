import {describe, it, expect} from "vitest";
import {render, fireEvent, screen, waitForElementToBeRemoved} from "../setup/react.jsx";
import CreateRestaurant from "../../pages/CreateRestaurant.jsx";

describe("CreateRestaurant", () => {
    it("should render the preview with the inserted title", async () => {
        const {container} = render(<CreateRestaurant/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const RESTAURANT_TITLE = "Lorem Ipsum Dolor Sit Amet";
        const titleInput = container.querySelector("input[name='name']");
        fireEvent.change(titleInput, {target: {value: RESTAURANT_TITLE}});
        expect(container.querySelector("h5.card-title").innerHTML).toEqual(RESTAURANT_TITLE);
    });
});
