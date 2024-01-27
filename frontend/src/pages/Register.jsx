import { useTranslation } from "react-i18next";
import {Link, useNavigate, useSearchParams} from "react-router-dom";
import Page from "../components/Page.jsx";
import "./styles/login.styles.css";
import {useContext, useEffect, useState} from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import {RegisterSchema} from "../data/validation.js";
import {useApi} from "../hooks/useApi.js";
import ApiContext from "../contexts/ApiContext.jsx";
import {useMutation} from "@tanstack/react-query";
import {useUserService} from "../hooks/services/useUserService.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {BAD_REQUEST_STATUS_CODE, EMAIL_ALREADY_IN_USE_ERROR, GET_LANGUAGE_CODE} from "../utils.js";

function Register() {
    const { t, i18n } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);
    const navigate = useNavigate();

    const [queryParams] = useSearchParams();
    const [emailAlreadyInUse, setEmailAlreadyInUse] = useState(false);

    const registerMutation = useMutation({
        mutationFn: async ({name, email, password}) => {
            await userService.register(
                apiContext.usersUrl,
                name,
                email,
                password,
                GET_LANGUAGE_CODE(i18n.language)
            );
        }
    });

    const handleRegister = (values, {setSubmitting}) => {
        registerMutation.mutate(
            {
                name: values.name,
                email: values.email,
                password: values.password
            },
            {
                onSuccess: () => navigate("/auth/login?alertType=success&alertMessage=login.register_success"),
                onError: (error) => {
                    if (error.response.status === BAD_REQUEST_STATUS_CODE) {
                        setEmailAlreadyInUse(error.response.data.some((error) => (
                            error.message === EMAIL_ALREADY_IN_USE_ERROR.message && error.path === EMAIL_ALREADY_IN_USE_ERROR.path
                        )));
                    }
                }
            }
        );
        setSubmitting(false);
    };

    useEffect(() => {
        if (authContext.isAuthenticated) {
            navigate("/");
        }
    }, [authContext.isAuthenticated, navigate]);

    return (
        <>
            <Page title={t("titles.signup")} className="login">
                <div className="card">
                    <div className="card-body">
                        <h2 className="card-title mb-3">{t("titles.signup")}</h2>
                        <Formik
                            initialValues={{
                                name: "",
                                email: queryParams.get("email") || "",
                                password: "",
                                repeatPassword: ""
                            }}
                            validationSchema={RegisterSchema}
                            onSubmit={handleRegister}
                        >
                            {({ isSubmitting }) => (
                                <Form>
                                    <div className="mb-3">
                                        <label htmlFor="name" className="form-label">{t("signup.name")}</label>
                                        <Field type="text" className="form-control" name="name" autoComplete="name" id="name"/>
                                        <ErrorMessage name="name" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="email" className="form-label">{t("signup.email_address")}</label>
                                        <Field type="email" className="form-control" name="email" autoComplete="email" id="email" onBlur={() => setEmailAlreadyInUse(false)}/>
                                        {emailAlreadyInUse && <div className="form-error">{t("validation.email.already_in_use")}</div>}
                                        <ErrorMessage name="email" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="password" className="form-label">{t("signup.password")}</label>
                                        <Field type="password" className="form-control" name="password" autoComplete="new-password" id="password"/>
                                        <ErrorMessage name="password" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="repeatPassword" className="form-label">{t("signup.repeat_password")}</label>
                                        <Field type="password" className="form-control" name="repeatPassword" autoComplete="new-password" id="repeatPassword"/>
                                        <ErrorMessage name="repeatPassword" className="form-error" component="div"/>
                                    </div>
                                    <button className="btn btn-primary" type="submit" disabled={isSubmitting}>{t("signup.button")}</button>
                                </Form>
                            )}
                        </Formik>
                        <p className="mt-3">{t("signup.already_have_account")} <Link to="/auth/login">{t("signup.login_here")}</Link></p>
                    </div>
                </div>
            </Page>
        </>
    );
}

export default Register;
