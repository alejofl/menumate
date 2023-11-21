import { useTranslation } from "react-i18next";
import { useTitle } from "../utils/useTitle.js";
import Logo from "../assets/logo.png";
import "./styles/error.styles.css";

function Error({errorNumber}) {
    const { t } = useTranslation();
    useTitle(t("titles.error", {number: errorNumber}));

    return (
        <>
            <h1 className="error-number">{errorNumber}</h1>
            <div className="error-container">
                <div className="error-image">
                    <img src={Logo} alt="MenuMate" height="40"/>
                </div>
                <div className="error-message">
                    <h1>{t(`error.${errorNumber}.title`)}</h1>
                </div>
                <div className="error-message">
                    <p>{t(`error.${errorNumber}.description`)}</p>
                </div>
                <div className="error-image">
                    <a href="TODO">
                        <button type="button" className="btn btn-primary btn-lg">{t("error.back_to_home")}</button>
                    </a>
                </div>
            </div>
        </>
    );
}

export default Error;

