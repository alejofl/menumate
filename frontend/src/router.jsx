import {
    createBrowserRouter
} from "react-router-dom";
import Home from "./pages/Home.jsx";
import Error from "./pages/Error.jsx";
import Restaurants from "./pages/Restaurants.jsx";

const router = createBrowserRouter(
    [
        {
            path: "/",
            Component: Home
        },
        {
            path: "/restaurants",
            Component: Restaurants
        },
        {
            path: "*",
            element: <Error errorNumber="404"/>
        }
    ],
    {
        basename: import.meta.env.BASE_URL
    }
);

export default router;
