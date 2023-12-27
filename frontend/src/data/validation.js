
import * as Yup from "yup";
import i18n from "../i18n";

export const LoginSchema = Yup.object().shape({
    email: Yup.string()
        .required(i18n.t("validation.email.required"))
        .matches(/^(([^<>()[\]\\.,;:\s@"]+(.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$/, i18n.t("validation.email.invalid")),

    password: Yup.string()
        .required(i18n.t("validation.password.required"))
});

export const EmailSchema = Yup.object().shape({
    email: Yup.string()
        .required(i18n.t("validation.email.required"))
        .matches(/^(([^<>()[\]\\.,;:\s@"]+(.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$/, i18n.t("validation.email.invalid"))
});
