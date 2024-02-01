import {describe, it, expect} from "vitest";
import {fireEvent, render, screen, waitForElementToBeRemoved, waitFor} from "../setup/react.jsx";
import Register from "../../pages/Register.jsx";
import {http, HttpResponse} from "msw";
import {apiUrl} from "../setup/utils.js";
import {EMAIL_ALREADY_IN_USE_ERROR, USER_CONTENT_TYPE} from "../../utils.js";

describe("Register", () => {
    it("should show an error when the passwords don't match", async () => {
        const {container} = render(<Register/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const passwordInput = container.querySelector("input[name='password']");
        const repeatPasswordInput = container.querySelector("input[name='repeatPassword']");
        fireEvent.change(passwordInput, {target: {value: "A very strong password"}});
        fireEvent.change(repeatPasswordInput, {target: {value: "A different password"}});
        fireEvent.click(container.querySelector("main > div > div > form > button"));
        await waitFor(() => expect(screen.getByText(/Passwords do not match/i)).toBeDefined());
    });

    it("should show an error when the email is already in use", async ({server}) => {
        server.use(
            http.post(apiUrl("/users"), ({request}) => {
                if (request.headers.get("Content-Type") === USER_CONTENT_TYPE) {
                    return new HttpResponse(
                        JSON.stringify([{path: EMAIL_ALREADY_IN_USE_ERROR}]),
                        {status: 400}
                    );
                } else {
                    return new HttpResponse(null, {status: 415});
                }
            })
        );

        const {container} = render(<Register/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        const nameInput = container.querySelector("input[name='name']");
        const emailInput = container.querySelector("input[name='email']");
        const passwordInput = container.querySelector("input[name='password']");
        const repeatPasswordInput = container.querySelector("input[name='repeatPassword']");
        fireEvent.change(nameInput, {target: {value: "John Doe"}});
        fireEvent.change(emailInput, {target: {value: "juansito@gmail.com"}});
        fireEvent.change(passwordInput, {target: {value: "A very strong password"}});
        fireEvent.change(repeatPasswordInput, {target: {value: "A very strong password"}});
        fireEvent.click(container.querySelector("main > div > div > form > button"));
        await waitFor(() => expect(screen.getByText(/Email is already in use/i)).toBeDefined());
    });
});
