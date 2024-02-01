import {describe, it, expect} from "vitest";
import {render, fireEvent, screen, waitForElementToBeRemoved, waitFor} from "../setup/react.jsx";
import Login from "../../pages/Login.jsx";

describe("Login", () => {
    it("should open the Forgot Password card when button clicked", async () => {
        const {container} = render(<Login/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const forgotPasswordButton = container.querySelector("main > div > div > span > button");
        fireEvent.click(forgotPasswordButton);
        await waitFor(() => expect(container.querySelector("h2").innerHTML).toEqual("I forgot my password"));
    });

    it("should show de Login card again when Go Back button clicked", async () => {
        const {container} = render(<Login/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const forgotPasswordButton = container.querySelector("main > div > div > span > button");
        fireEvent.click(forgotPasswordButton);
        const goBackButton = container.querySelector("main > div > div > button");
        fireEvent.click(goBackButton);
        await waitFor(() => expect(container.querySelector("h2").innerHTML).toEqual("Login"));
    });
});
