import {
    createBrowserRouter
} from "react-router-dom";
import Home from "./pages/Home.jsx";

const router = createBrowserRouter(
    [
        {
            path: "/",
            Component: Home
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
