import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import router from "./router.jsx";
import { RouterProvider } from "react-router-dom";
import "./i18n";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import {AppWrapper} from "./contexts/AppWrapper.jsx";
import {ApiContextProvider} from "./contexts/ApiContext.jsx";
import {AuthContextProvider} from "./contexts/AuthContext.jsx";

ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
        <QueryClientProvider
            client={new QueryClient({
                defaultOptions: {
                    queries: {
                        retry: false
                    }
                }
            })}
        >
            <ApiContextProvider>
                <AppWrapper>
                    <AuthContextProvider>
                        <RouterProvider router={router}/>
                    </AuthContextProvider>
                </AppWrapper>
            </ApiContextProvider>
        </QueryClientProvider>
    </React.StrictMode>
);
