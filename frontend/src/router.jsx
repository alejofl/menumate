import {
    createBrowserRouter
} from "react-router-dom";

import App from "./App";

const router = createBrowserRouter(
    [
        {
            path: "/",
            Component: App
        },
        {
            path: "*",
            element: <h1>Error</h1>
        }
    ],
    {
        basename: import.meta.env.BASE_URL
    }
);

export default router;
