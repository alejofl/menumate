import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {selectComponents, selectStyles} from "../components/utils/SelectProperties.js";
import Select from "react-select";
import RestaurantSpecialties from "../data/RestaurantSpecialties.js";
import RestaurantTags from "../data/RestaurantTags.js";
import {CreateRestaurantSchema} from "../data/validation.js";
import ImagePlaceholder from "../assets/image-placeholder.png";
import {useMutation} from "@tanstack/react-query";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {useApi} from "../hooks/useApi.js";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import "./styles/create_restaurant.styles.css";

function CreateRestaurant() {
    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const restaurantService = useRestaurantService(api);

    const specialties = RestaurantSpecialties.map(specialty => ({
        label: t(`restaurant_specialties.${specialty}`),
        value: specialty
    }));
    const tags = RestaurantTags.map(tag => ({
        label: t(`restaurant_tags.${tag}`),
        value: tag
    }));

    const createRestaurantMutation = useMutation({
        mutationFn: async ({
            name,
            address,
            specialty,
            tags,
            description,
            maxTables,
            logo,
            portrait1,
            portrait2
        }) => {
            await restaurantService.createRestaurant(
                apiContext.restaurantsUrl,
                name,
                address,
                specialty,
                tags,
                description,
                maxTables,
                logo,
                portrait1,
                portrait2
            );
        }
    });

    const [showErrorAlert, setShowErrorAlert] = useState(false);

    const handleSubmit = (values, {setSubmitting, resetForm}) => {
        createRestaurantMutation.mutate(
            {
                name: values.name,
                address: values.address,
                specialty: values.specialty,
                tags: values.tags,
                description: values.description,
                maxTables: values.maxTables,
                logo: values.logo,
                portrait1: values.portrait1,
                portrait2: values.portrait2
            },
            {
                onSuccess: () => {
                    // TODO - Redirect to edit menu page. To do it we need to get the restaurantId from the response.
                    resetForm();
                },
                onError: () => setShowErrorAlert(true)
            }
        );
        setSubmitting(false);
    };

    return (
        <>
            <Page title={t("titles.create_restaurant")} className="create_restaurant">
                <Formik
                    initialValues={{
                        name: "",
                        address: "",
                        specialty: "",
                        tags: [],
                        description: "",
                        maxTables: "",
                        logo: "",
                        portrait1: "",
                        portrait2: ""
                    }}
                    validationSchema={CreateRestaurantSchema}
                    onSubmit={handleSubmit}
                >
                    {({values, isSubmitting}) => (
                        <>
                            <div className="flex-grow-1">
                                <h2 className="mb-3">{t("create_restaurant.title")}</h2>
                                {showErrorAlert && <div className="alert alert-danger" role="alert">{t("create_restaurant.error")}</div>}
                                <Form>
                                    <div className="mb-3">
                                        <label htmlFor="name" className="form-label">{t("create_restaurant.name")}</label>
                                        <Field name="name" type="text" className="form-control" id="name"/>
                                        <ErrorMessage name="name" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="address" className="form-label">{t("create_restaurant.address")}</label>
                                        <Field name="address" type="text" className="form-control" id="address"/>
                                        <ErrorMessage name="address" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="specialty" className="form-label">{t("create_restaurant.specialty")}</label>
                                        <Field name="specialty">
                                            {({field, form}) => (
                                                <Select
                                                    placeholder={t("create_restaurant.specialty_placeholder")}
                                                    styles={selectStyles(false, false)}
                                                    components={selectComponents}
                                                    options={specialties}
                                                    closeMenuOnSelect={true}
                                                    id="specialty"
                                                    name={field.name}
                                                    value={specialties.find(option => option.value === field.value)}
                                                    onChange={option => form.setFieldValue(field.name, option.value)}
                                                    onBlur={field.onBlur}
                                                />
                                            )}
                                        </Field>
                                        <ErrorMessage name="specialty" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="tags" className="form-label">{t("create_restaurant.tags")}</label>
                                        <Field name="tags">
                                            {({field, form}) => (
                                                <Select
                                                    placeholder={t("create_restaurant.tags_placeholder")}
                                                    styles={selectStyles(false, false)}
                                                    components={selectComponents}
                                                    options={tags}
                                                    closeMenuOnSelect={false}
                                                    isMulti
                                                    id="tags"
                                                    name={field.name}
                                                    value={tags.filter(option => field.value.includes(option.value))}
                                                    onChange={options => form.setFieldValue(field.name, options.map(option => option.value))}
                                                    onBlur={field.onBlur}
                                                />
                                            )}
                                        </Field>
                                        <ErrorMessage name="tags" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="description" className="form-label">{t("create_restaurant.description")}</label>
                                        <Field as="textarea" name="description" className="form-control" id="description" rows="3"/>
                                        <ErrorMessage name="description" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="maxTables" className="form-label">{t("create_restaurant.tables_quantity")}</label>
                                        <Field name="maxTables" type="number" className="form-control" id="maxTables"/>
                                        <ErrorMessage name="maxTables" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="logo" className="form-label">{t("create_restaurant.logo")}</label>
                                        <Field name="logo">
                                            {({field: {value, onChange, ...field}, form}) => (
                                                <input
                                                    type="file"
                                                    className="form-control"
                                                    id="logo"
                                                    accept="image/*"
                                                    {...field}
                                                    onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                />
                                            )}
                                        </Field>
                                        <ErrorMessage name="logo" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="portrait1" className="form-label">{t("create_restaurant.portrait1")}</label>
                                        <Field name="portrait1">
                                            {({field: {value, onChange, ...field}, form}) => (
                                                <input
                                                    type="file"
                                                    className="form-control"
                                                    id="portrait1"
                                                    accept="image/*"
                                                    {...field}
                                                    onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                />
                                            )}
                                        </Field> <ErrorMessage name="portrait1" className="form-error" component="div"/>
                                    </div>
                                    <div className="mb-3">
                                        <label htmlFor="portrait2" className="form-label">{t("create_restaurant.portrait2")}</label>
                                        <Field name="portrait2">
                                            {({field: {value, onChange, ...field}, form}) => (
                                                <input
                                                    type="file"
                                                    className="form-control"
                                                    id="portrait2"
                                                    accept="image/*"
                                                    {...field}
                                                    onChange={event => form.setFieldValue(field.name, event.currentTarget.files[0])}
                                                />
                                            )}
                                        </Field>
                                        <ErrorMessage name="portrait2" className="form-error" component="div"/>
                                    </div>
                                    <div className="mt-4">
                                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("create_restaurant.title")}</button>
                                    </div>
                                </Form>
                            </div>
                            <div className="d-flex flex-column align-items-center ms-4 sticky">
                                <h2>{t("create_restaurant.preview")}</h2>
                                <div className="restaurant_card">
                                    <div className="card">
                                        <div
                                            className="card-img"
                                            style={{
                                                "--main_image": `url(${values.portrait1 ? URL.createObjectURL(values.portrait1) : ImagePlaceholder})`,
                                                "--hover_image": `url(${values.portrait2 ? URL.createObjectURL(values.portrait2) : ImagePlaceholder})`
                                            }}
                                        />
                                        <div className="card-body">
                                            <div>
                                                <h5 className="card-title">{values.name || t("create_restaurant.name")}</h5>
                                                <p className="card-text m-0">{values.address || t("create_restaurant.address")}</p>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </>
                    )}
                </Formik>
            </Page>
        </>
    );
}

export default CreateRestaurant;
