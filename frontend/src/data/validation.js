/* eslint-disable no-magic-numbers */

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

export const ResetPasswordSchema = Yup.object().shape({
    newPassword: Yup.string()
        .required(i18n.t("validation.password.required"))
        .min(8, ({ min }) => i18n.t("validation.password.min", {min: min}))
        .max(72, ({ max }) => i18n.t("validation.password.max", {max: max})),

    repeatPassword: Yup.string()
        .required(i18n.t("validation.repeat_password.required"))
        .oneOf([Yup.ref("newPassword")], i18n.t("validation.repeat_password.match"))
});
