import {render} from "@testing-library/react";
import {QueryClient, QueryClientProvider} from "@tanstack/react-query";
import {ApiContextProvider} from "../../contexts/ApiContext.jsx";
import {AuthContextProvider} from "../../contexts/AuthContext.jsx";

// eslint-disable-next-line react-refresh/only-export-components
const AppWrapper = ({children}) => {
    return (
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
                        {children}
                    </AuthContextProvider>
                </AppWrapper>
            </ApiContextProvider>
        </QueryClientProvider>
    );
};

const customRender = (ui, options) => render(ui, {wrapper: AppWrapper, ...options});

// eslint-disable-next-line react-refresh/only-export-components
export * from "@testing-library/react";
export {customRender as render};
