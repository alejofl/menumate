import {
    createBrowserRouter
} from "react-router-dom";
import Home from "./pages/Home.jsx";
import Error from "./pages/Error.jsx";
import Restaurants from "./pages/Restaurants.jsx";
import Login from "./pages/Login.jsx";
import ResetPassword from "./pages/ResetPassword.jsx";
import Register from "./pages/Register.jsx";
import Restaurant from "./pages/Restaurant.jsx";
import VerifyAccount from "./pages/VerifyAccount.jsx";

const router = createBrowserRouter(
    [
        {
            path: "/",
            Component: Home
        },
        {
            path: "/auth/login",
            Component: Login
        },
        {
            path: "/auth/register",
            Component: Register
        },
        {
            path: "/auth/reset-password",
            Component: ResetPassword
        },
        {
            path: "/auth/verify",
            Component: VerifyAccount
        },
        {
            path: "/restaurants",
            Component: Restaurants
        },
        {
            path: "/restaurants/:restaurantId",
            Component: Restaurant
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
