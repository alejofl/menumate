import ApiContext from "./ApiContext.jsx";
import { useContext } from "react";

export function AppWrapper({ children }) {
    const { didDiscovery } = useContext(ApiContext);

    if (!didDiscovery) {
        return (
            <>
                <div className="d-flex align-items-center justify-content-center flex-grow-1">
                    <div className="spinner-border" role="status">
                        <span className="visually-hidden">Loading...</span>
                    </div>
                </div>
            </>
        );
    }

    return (
        <>
            {children}
        </>
    );
}
