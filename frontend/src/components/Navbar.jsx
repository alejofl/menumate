import { useState } from "react";
import { useTranslation } from "react-i18next";
import "./styles/navbar.styles.css";
import Logo from "../assets/logo.png";

function Navbar() {
    const { t } = useTranslation();
    const [loggedIn] = useState(true);
    const [isModerator] = useState(true);

    return (
        <>
            <nav className="navbar navbar-expand-md bg-body-tertiary sticky-top" data-bs-theme="dark">
                <a className="navbar-brand" href="TODO">
                    <img src={Logo} alt="MenuMate" height="24"/>
                </a>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse justify-content-between" id="navbarNav">
                    <ul className="navbar-nav">
                        <li className="nav-item">
                            <a className="nav-link active" aria-current="page" href="TODO">{t("navbar.explore")}</a>
                        </li>
                    </ul>
                    <div className="d-flex gap-4">
                        {loggedIn
                            ?
                            <>
                                {isModerator && <a href="TODO" type="button" className="btn btn-accent">{t("navbar.moderator_pane")}</a>}
                                <div className="text-color-white">
                                    <div className="dropdown">
                                        <button className="btn btn-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                            <i className="bi bi-person-circle"></i> Pepe
                                        </button>
                                        <ul className="dropdown-menu dropdown-menu-end">
                                            <li><a className="dropdown-item" href="TODO">{t("navbar.my_profile")}</a></li>
                                            <li><a className="dropdown-item" href="TODO">{t("navbar.my_orders")}</a></li>
                                            <li><hr className="dropdown-divider"/></li>
                                            <li><a className="dropdown-item" href="TODO">{t("navbar.create_restaurant")}</a></li>
                                            <li><a className="dropdown-item" href="TODO">{t("navbar.my_restaurants")}</a></li>
                                            <li><hr className="dropdown-divider"/></li>
                                            <li><a className="dropdown-item" href="TODO">{t("navbar.logout")}</a></li>
                                        </ul>
                                    </div>
                                </div>
                            </>
                            :
                            <ul className="navbar-nav">
                                <li className="nav-item">
                                    <a className="nav-link active" aria-current="page" href="TODO">{t("navbar.login")}</a>
                                </li>
                                <li className="nav-item">
                                    <a className="nav-link active" aria-current="page" href="TODO">{t("navbar.signup")}</a>
                                </li>
                            </ul>
                        }
                    </div>
                </div>
            </nav>
        </>
    );
}

export default Navbar;
