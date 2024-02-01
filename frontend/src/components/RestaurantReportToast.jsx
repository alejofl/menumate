import {useEffect, useState} from "react";
import {useTranslation} from "react-i18next";
import "./styles/restaurant_toast.styles.css";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {ReportSchema} from "../data/validation.js";
import {useMutation} from "@tanstack/react-query";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import {Toast} from "bootstrap";

function RestaurantReportToast({ reportsUrl }) {
    const { t } = useTranslation();
    const api = useApi();
    const restaurantService = useRestaurantService(api);

    const showToast = (querySelector) => {
        const toast = Toast.getOrCreateInstance(document.querySelector(querySelector));
        if (!toast.isShown()) {
            toast.show();
        }
    };

    const [showErrorAlert, setShowErrorAlert] = useState(false);
    const [showSuccessAlert, setShowSuccessAlert] = useState(false);

    useEffect(() => {
        showToast(".restaurant_report_toast #icon-toast");

        document.querySelector("#restaurant-report-modal").addEventListener("hidden.bs.modal", () => {
            setShowErrorAlert(false);
            setShowSuccessAlert(false);
        });
    }, []);

    const reportRestaurantMutation = useMutation({
        mutationFn: async ({comment}) => {
            await restaurantService.reportRestaurant(
                reportsUrl,
                comment
            );
        }
    });

    const handleReportRestaurant = (values, {setSubmitting, resetForm}) => {
        reportRestaurantMutation.mutate(
            {
                comment: values.comment
            },
            {
                onSuccess: () => {
                    setShowSuccessAlert(true);
                    resetForm();
                },
                onError: () => setShowErrorAlert(true)
            }
        );
        setSubmitting(false);
    };

    return (
        <>
            <div className="restaurant_report_toast">
                <div className="toast-container p-3 bottom-0 start position-fixed restaurant-menu-toasts d-flex flex-column align-items-start">
                    <div className="toast" role="alert" id="text-toast" aria-live="assertive" aria-atomic="true" data-bs-autohide="false">
                        <div className="d-flex">
                            <div className="toast-body">
                                <span>{t("restaurant.toasts.report.text")} {t("restaurant.toasts.click")} </span>
                                <a className="link-warning" data-bs-toggle="modal" data-bs-target="#restaurant-report-modal">{t("restaurant.toasts.here")}</a>
                                <span> {t("restaurant.toasts.report.click")}</span>
                            </div>
                            <button type="button" className="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                        </div>
                    </div>
                    <div className="toast text-bg-warning" id="icon-toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false" onClick={() => showToast(".restaurant_report_toast #text-toast")}>
                        <div className="d-flex">
                            <div className="toast-body">
                                <h5 className="m-0">
                                    <i className="bi bi-exclamation-triangle-fill default"></i>
                                </h5>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="modal fade" id="restaurant-report-modal" tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{t("restaurant.report.title")}</h1>
                            <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <Formik
                            initialValues={{
                                comment: ""
                            }}
                            validationSchema={ReportSchema}
                            onSubmit={handleReportRestaurant}
                        >
                            {({isSubmitting}) => (
                                <Form>
                                    <div className="modal-body">
                                        {
                                            showErrorAlert && <div className="alert alert-danger fade show" role="alert">{t("restaurant.report.error")}</div>
                                        }
                                        {
                                            showSuccessAlert &&
                                            <div className="alert alert-success" role="alert">
                                                {t("restaurant.report.success")} <a className="alert-link" data-bs-dismiss="modal">{t("restaurant.report.success_button")}</a>.
                                            </div>
                                        }
                                        <div className="mb-3">
                                            <label htmlFor="comment" className="form-label">{t("restaurant.report.comment")}</label>
                                            <Field as="textarea" className="form-control" name="comment" id="comment" rows="3"/>
                                            <ErrorMessage name="comment" className="form-error" component="div"/>
                                        </div>
                                    </div>
                                    <div className="modal-footer">
                                        <button type="submit" className="btn btn-accent" disabled={isSubmitting}>{t("restaurant.report.button")}</button>
                                    </div>
                                </Form>
                            )}
                        </Formik>
                    </div>
                </div>
            </div>
        </>
    );
}

export default RestaurantReportToast;
