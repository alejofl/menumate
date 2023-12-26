import { useState } from "react";
import { useTranslation } from "react-i18next";
import "./styles/navbar.styles.css";
import Logo from "../assets/logo.png";
import { Link } from "react-router-dom";

function Navbar() {
    const { t } = useTranslation();
    const [loggedIn] = useState(true);
    const [isModerator] = useState(true);

    return (
        <div className="navbar_component">
            <nav className="navbar navbar-expand-md bg-body-tertiary sticky-top" data-bs-theme="dark">
                <Link className="navbar-brand" to="/">
                    <img src={Logo} alt="MenuMate" height="24"/>
                </Link>
                <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
                    <span className="navbar-toggler-icon"></span>
                </button>
                <div className="collapse navbar-collapse justify-content-between" id="navbarNav">
                    <ul className="navbar-nav">
                        <li className="nav-item">
                            <Link className="nav-link active" aria-current="page" to="/restaurants">{t("navbar.explore")}</Link>
                        </li>
                    </ul>
                    <div className="d-flex gap-4">
                        {loggedIn
                            ?
                            <>
                                {isModerator && <Link to="/moderators" type="button" className="btn btn-accent">{t("navbar.moderator_pane")}</Link>}
                                <div className="text-color-white">
                                    <div className="dropdown">
                                        <button className="btn btn-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                            <i className="bi bi-person-circle"></i> Pepe
                                        </button>
                                        <ul className="dropdown-menu dropdown-menu-end">
                                            <li><Link className="dropdown-item" to="/user">{t("navbar.my_profile")}</Link></li>
                                            <li><Link className="dropdown-item" to="/user/orders">{t("navbar.my_orders")}</Link></li>
                                            <li><hr className="dropdown-divider"/></li>
                                            <li><Link className="dropdown-item" to="/restaurants/create">{t("navbar.create_restaurant")}</Link></li>
                                            <li><Link className="dropdown-item" to="/user/restaurants">{t("navbar.my_restaurants")}</Link></li>
                                            <li><hr className="dropdown-divider"/></li>
                                            <li><Link className="dropdown-item" to="/auth/logout">{t("navbar.logout")}</Link></li>
                                        </ul>
                                    </div>
                                </div>
                            </>
                            :
                            <ul className="navbar-nav">
                                <li className="nav-item">
                                    <Link className="nav-link active" aria-current="page" to="/auth/login">{t("navbar.login")}</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link active" aria-current="page" to="/auth/signup">{t("navbar.signup")}</Link>
                                </li>
                            </ul>
                        }
                    </div>
                </div>
            </nav>
        </div>
    );
}

export default Navbar;
