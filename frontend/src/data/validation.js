/* eslint-disable no-magic-numbers */

import * as Yup from "yup";
import i18n from "../i18n";
import {ORDER_TYPE} from "../utils.js";

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

export const RegisterSchema = Yup.object().shape({
    name: Yup.string()
        .required(i18n.t("validation.name.required"))
        .min(2, ({ min }) => i18n.t("validation.name.min", {min: min}))
        .max(48, ({ max }) => i18n.t("validation.name.max", {max: max})),

    email: Yup.string()
        .required(i18n.t("validation.email.required"))
        .matches(/^(([^<>()[\]\\.,;:\s@"]+(.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$/, i18n.t("validation.email.invalid")),

    password: Yup.string()
        .required(i18n.t("validation.password.required"))
        .min(8, ({ min }) => i18n.t("validation.password.min", {min: min}))
        .max(72, ({ max }) => i18n.t("validation.password.max", {max: max})),

    repeatPassword: Yup.string()
        .required(i18n.t("validation.repeat_password.required"))
        .oneOf([Yup.ref("password")], i18n.t("validation.repeat_password.match"))
});

export const PlaceOrderSchema = (orderType, maxTables) => Yup.object().shape({
    name: Yup.string()
        .required(i18n.t("validation.name.required"))
        .min(2, ({ min }) => i18n.t("validation.name.min", {min: min}))
        .max(48, ({ max }) => i18n.t("validation.name.max", {max: max})),

    email: Yup.string()
        .required(i18n.t("validation.email.required"))
        .matches(/^(([^<>()[\]\\.,;:\s@"]+(.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$/, i18n.t("validation.email.invalid")),

    tableNumber: orderType === ORDER_TYPE.DINE_IN
        ?
        Yup.number()
            .required(i18n.t("validation.table_number.required"))
            .min(1, ({ min }) => i18n.t("validation.table_number.min", {min: min}))
            .max(maxTables, ({ value }) => i18n.t("validation.table_number.max", {value: value}))
        :
        Yup.number().nullable(),

    address: orderType === ORDER_TYPE.DELIVERY
        ?
        Yup.string()
            .required(i18n.t("validation.address.required"))
            .min(3, ({ min }) => i18n.t("validation.address.min", {min: min}))
            .max(120, ({ max }) => i18n.t("validation.address.max", {max: max}))
        :
        Yup.string().nullable()
});
