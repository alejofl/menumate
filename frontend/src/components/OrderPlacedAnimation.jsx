import "./styles/order_placed_animation.styles.css";
import LogoWhite from "../assets/logo-white.png";
import {useTitle} from "../hooks/useTitle.js";
import {useTranslation} from "react-i18next";
import {Link} from "react-router-dom";

function OrderPlacedAnimation() {
    const { t } = useTranslation();

    useTitle(t("titles.order_placed"));

    return (
        <div className="order_placed_animation">
            <div className="position-relative">
                <svg className="circle centered" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
                    <circle className="circle-circle" cx="26" cy="26" r="25" fill="none"></circle>
                </svg>
                <svg className="checkmark centered" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 52 52">
                    <circle className="checkmark-circle" cx="26" cy="26" r="25" fill="none"></circle>
                    <path className="checkmark-check" fill="none" d="M14.1 27.2l7.1 7.2 16.7-16.8"></path>
                </svg>
                <div className="thank-you-container centered">
                    <div className="message m-4">
                        <img src={LogoWhite} alt="MenuMate" height="40"/>
                    </div>
                    <div className="message text-white">
                        <h1>{t("order_placed.title")}</h1>
                    </div>
                    <div className="message text-white">
                        <p>{t("order_placed.description")}</p>
                    </div>
                    <div className="m-2">
                        <Link type="button" className="btn btn-primary-inverted" to="/user/orders">
                            {t("order_placed.button")}
                        </Link>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default OrderPlacedAnimation;
