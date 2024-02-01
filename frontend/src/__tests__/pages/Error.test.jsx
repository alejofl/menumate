import {describe, it, expect} from "vitest";
import {render, screen, waitForElementToBeRemoved} from "../setup/react.jsx";
import Error from "../../pages/Error.jsx";

describe("Error", () => {
    it("should show the error number on screen", async () => {
        render(<Error errorNumber={401}/>);
        await waitForElementToBeRemoved(() => screen.getByText(/loading/i));

        expect(screen.getAllByText(/401/i)).toBeDefined();
    });
});
