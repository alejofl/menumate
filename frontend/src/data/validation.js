/* eslint-disable no-magic-numbers */

import * as Yup from "yup";
import i18n from "../i18n";
import {IMAGE_MAX_SIZE, ORDER_TYPE, ROLE_FOR_RESTAURANT} from "../utils.js";
import RestaurantTags from "./RestaurantTags.js";
import RestaurantSpecialties from "./RestaurantSpecialties.js";

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

export const ReportSchema = Yup.object().shape({
    comment: Yup.string()
        .required(i18n.t("validation.comment.required"))
        .min(1, i18n.t("validation.comment.required"))
        .max(500, ({ max }) => i18n.t("validation.comment.max", {max: max}))
});


export const RegisterAddressSchema = Yup.object().shape({
    name: Yup.string()
        .max(20, ({max}) => i18n.t("validation.my_profile.register_address_name_max", {max: max})),

    address: Yup.string()
        .required(i18n.t("validation.my_profile.required"))
        .min(3, ({ min }) => i18n.t("validation.address.min", {min: min}))
        .max(200, ({max}) => i18n.t("validation.my_profile.register_address_address_max", {max: max}))
});

export const CreateRestaurantSchema = (edit) => Yup.object().shape({
    name: Yup.string()
        .required(i18n.t("validation.restaurant_name.required"))
        .max(50, ({ max }) => i18n.t("validation.restaurant_name.max", {max: max})),

    address: Yup.string()
        .required(i18n.t("validation.restaurant_address.required"))
        .max(200, ({ max }) => i18n.t("validation.restaurant_address.max", {max: max})),

    specialty: Yup.string()
        .required(i18n.t("validation.restaurant_specialty.required"))
        .oneOf(RestaurantSpecialties, i18n.t("validation.restaurant_specialty.invalid")),

    tags: Yup.array()
        .required(i18n.t("validation.restaurant_tags.required"))
        .min(1, i18n.t("validation.restaurant_tags.required"))
        .of(Yup.string().oneOf(RestaurantTags, i18n.t("validation.restaurant_tags.invalid"))),

    description: Yup.string()
        .required(i18n.t("validation.restaurant_description.required"))
        .max(300, ({ max }) => i18n.t("validation.restaurant_description.max", {max: max})),

    maxTables: Yup.number()
        .required(i18n.t("validation.restaurant_max_tables.required"))
        .min(1, ({ min }) => i18n.t("validation.restaurant_max_tables.min", {min: min})),

    logo: (edit ? Yup.mixed().notRequired() : Yup.mixed().required(i18n.t("validation.image.required")))
        .test("fileSize", i18n.t("validation.image.size"), (value) => (edit && !value) || (value && value.size <= IMAGE_MAX_SIZE))
        .test("fileType", i18n.t("validation.image.type"), (value) => (edit && !value) || (value && value.type.startsWith("image/"))),

    portrait1: (edit ? Yup.mixed().notRequired() : Yup.mixed().required(i18n.t("validation.image.required")))
        .test("fileSize", i18n.t("validation.image.size"), (value) => (edit && !value) || (value && value.size <= IMAGE_MAX_SIZE))
        .test("fileType", i18n.t("validation.image.type"), (value) => (edit && !value) || (value && value.type.startsWith("image/"))),

    portrait2: (edit ? Yup.mixed().notRequired() : Yup.mixed().required(i18n.t("validation.image.required")))
        .test("fileSize", i18n.t("validation.image.size"), (value) => (edit && !value) || (value && value.size <= IMAGE_MAX_SIZE))
        .test("fileType", i18n.t("validation.image.type"), (value) => (edit && !value) || (value && value.type.startsWith("image/")))
});

export const ReviewReplySchema = Yup.object().shape({
    reply: Yup.string()
        .required(i18n.t("validation.review_reply.required"))
        .max(500, ({ max }) => i18n.t("validation.review_reply.max", {max: max}))
});

export const AddModeratorSchema = Yup.object().shape({
    email: Yup.string()
        .required(i18n.t("validation.email.required"))
        .matches(/^(([^<>()[\]\\.,;:\s@"]+(.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$/, i18n.t("validation.email.invalid"))
});

export const ReviewSchema = Yup.object().shape({
    comment: Yup.string()
        .required(i18n.t("validation.review_comment.required"))
        .max(500, ({ max }) => i18n.t("validation.review_comment.max", {max: max}))
});

export const AddCategorySchema = Yup.object().shape({
    name: Yup.string()
        .required(i18n.t("validation.category_name.required"))
        .min(1, ({ min }) => i18n.t("validation.category_name.min", {min: min}))
        .max(50, ({ max }) => i18n.t("validation.category_name.max", {max: max}))
});

export const AddProductSchema = Yup.object().shape({
    productName: Yup.string()
        .required(i18n.t("validation.product_name.required"))
        .min(1, ({ min }) => i18n.t("validation.product_name.min", {min: min}))
        .max(150, ({ max }) => i18n.t("validation.product_name.max", {max: max})),

    description: Yup.string()
        .notRequired()
        .max(300, ({ max }) => i18n.t("validation.product_description.max", {max: max})),

    price: Yup.number()
        .required(i18n.t("validation.product_price.required"))
        .min(0.01, ({ min }) => i18n.t("validation.product_price.min", {min: min}))
        .max(99999999, ({ max }) => i18n.t("validation.product_price.max", {max: max})),

    image: Yup.mixed()
        .notRequired()
        .test("fileSize", i18n.t("validation.image.size"), (value) => !value || (value && value.size <= IMAGE_MAX_SIZE))
        .test("fileType", i18n.t("validation.image.type"), (value) => !value || (value && value.type.startsWith("image/")))
});

export const AddEmployeeSchema = Yup.object().shape({
    email: Yup.string()
        .required(i18n.t("validation.email.required"))
        .matches(/^(([^<>()[\]\\.,;:\s@"]+(.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@(([[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}])|(([a-zA-Z-0-9]+.)+[a-zA-Z]{2,}))$/, i18n.t("validation.email.invalid")),

    role: Yup.string()
        .required(i18n.t("validation.role.required"))
        .oneOf([ROLE_FOR_RESTAURANT.ADMIN, ROLE_FOR_RESTAURANT.ORDER_HANDLER], i18n.t("validation.role.invalid"))
});
