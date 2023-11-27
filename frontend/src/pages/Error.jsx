import { useTranslation } from "react-i18next";
import { useTitle } from "../hooks/useTitle.js";
import Logo from "../assets/logo.png";
import "./styles/error.styles.css";
import { Link } from "react-router-dom";

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
                    <Link to="/">
                        <button type="button" className="btn btn-primary btn-lg">{t("error.back_to_home")}</button>
                    </Link>
                </div>
            </div>
        </>
    );
}

export default Error;

