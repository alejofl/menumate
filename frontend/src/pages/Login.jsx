import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import Page from "../components/Page.jsx";
import "./styles/login.styles.css";

function Login() {
    const { t } = useTranslation();

    return (
        <>
            <Page title={t("titles.login")} className="login">
                <div className="card">
                    <div className="card-body">
                        <h2 className="card-title mb-3">{t("titles.login")}</h2>
                        <div>
                            <div className="mb-3">
                                <label htmlFor="login-email" className="form-label">{t("login.email_address")}</label>
                                <input type="email" className="form-control" name="email" id="login-email"/>
                            </div>
                            <div className="mb-3">
                                <label htmlFor="login-password" className="form-label">{t("login.password")}</label>
                                <input type="password" className="form-control" name="password" id="login-password"/>
                            </div>
                            <div className="mb-3 form-check">
                                <input type="checkbox" className="form-check-input" name="rememberme" id="rememberme-checkbox"/>
                                <label className="form-check-label" htmlFor="rememberme-checkbox">{t("login.remember_me")}</label>
                            </div>
                            <button className="btn btn-primary btn-block" id="login-submit">{t("login.login_button")}</button>
                        </div>
                        <p className="mt-3">{t("login.no_account")} <Link to="auth/register">{t("login.signup_here")}</Link></p>
                        <span className="mt-3 more-actions">
                            <button type="button" className="btn btn-link">{t("login.forgot_password")}</button>
                            |
                            <button type="button" className="btn btn-link">{t("login.resend_verification")}</button>
                        </span>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default Login;
