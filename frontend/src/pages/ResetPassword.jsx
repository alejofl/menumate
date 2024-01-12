import { useTranslation } from "react-i18next";
import {useNavigate, useSearchParams} from "react-router-dom";
import Page from "../components/Page.jsx";
import "./styles/login.styles.css";
import {useContext, useEffect} from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import {ResetPasswordSchema} from "../data/validation.js";
import {useApi} from "../hooks/useApi.js";
import {useMutation} from "@tanstack/react-query";
import {useUserService} from "../hooks/services/useUserService.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {WAIT_TIME} from "../utils.js";

function ResetPassword() {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const api = useApi();
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);

    const resetPasswordMutation = useMutation({
        mutationFn: async (password) => {
            await userService.resetPassword(
                authContext.selfUrl,
                password
            );
        }
    });

    const [queryParams] = useSearchParams();

    const handleResetPassword = (values, {setSubmitting}) => {
        const errorUrl = "/auth/login?alertType=danger&alertMessage=login.reset_password_error";
        authContext.login(queryParams.get("email"), queryParams.get("token"), false)
            .then(success => {
                if (!success) {
                    return navigate(errorUrl);
                }
                setTimeout(() => {
                    resetPasswordMutation.mutate(
                        values.newPassword,
                        {
                            onSuccess: () => navigate("/"),
                            onError: () => navigate(errorUrl)
                        }
                    );
                }, WAIT_TIME);
            })
            .catch(() => navigate(errorUrl));
        setSubmitting(false);
    };

    useEffect(() => {
        if (!queryParams.has("token") || !queryParams.has("email")) {
            return navigate("/auth/login?alertType=danger&alertMessage=login.request_error");
        }
    }, []);

    return (
        <>
            <Page title={t("titles.reset_password")} className="login">
                <div className="card">
                    <div className="card-body">
                        <h2 className="card-title mb-3">{t("titles.reset_password")}</h2>
                        <Formik
                            initialValues={{
                                newPassword: "",
                                repeatPassword: ""
                            }}
                            validationSchema={ResetPasswordSchema}
                            onSubmit={handleResetPassword}
                        >
                            {({ isSubmitting }) => (
                                <Form>
                                    <div className="mb-3">
                                        <label htmlFor="newPassword" className="form-label">{t("reset_password.new_password")}</label>
                                        <Field type="password" className="form-control" name="newPassword" autoComplete="new-password" id="newPassword"/>
                                        <ErrorMessage name="newPassword" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="repeatPassword" className="form-label">{t("reset_password.repeat_password")}</label>
                                        <Field type="password" className="form-control" name="repeatPassword" autoComplete="new-password" id="repeatPassword"/>
                                        <ErrorMessage name="repeatPassword" className="form-error" component="div"/>
                                    </div>
                                    <button className="btn btn-primary" type="submit" disabled={isSubmitting}>
                                        {
                                            isSubmitting
                                                ?
                                                <>
                                                    <span className="spinner-border spinner-border-sm mx-4" role="status"></span>
                                                    <span className="visually-hidden">Loading...</span>
                                                </>
                                                :
                                                t("reset_password.button")}
                                    </button>
                                </Form>
                            )}
                        </Formik>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default ResetPassword;
