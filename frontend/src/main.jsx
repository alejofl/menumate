import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import router from "./router.jsx";
import { RouterProvider } from "react-router-dom";
import "./i18n";
import { ApiContextProvider } from "./contexts/ApiContext.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <ApiContextProvider>
            <RouterProvider router={router}/>
        </ApiContextProvider>
    </React.StrictMode>
);
