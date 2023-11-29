import ApiContext from "./ApiContext.jsx";
import { useContext } from "react";
import Logo from "../assets/logo.png";
import WifiIcon from "../assets/wifi-slash.png";
import { useTranslation } from "react-i18next";
import "../pages/styles/error.styles.css";

export function AppWrapper({ children }) {
    const { t } = useTranslation();
    const { didDiscovery, failedDiscovery } = useContext(ApiContext);

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

    if (failedDiscovery) {
        return (
            <>
                <img src={WifiIcon} alt="Error" className="error-number" style={{width: "40%", top: "50%"}}></img>
                <div className="error-container">
                    <div className="error-image">
                        <img src={Logo} alt="MenuMate" height="40"/>
                    </div>
                    <div className="error-message">
                        <h1>{t("error.api.title")}</h1>
                    </div>
                    <div className="error-message">
                        <p>{t("error.api.description")}</p>
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
