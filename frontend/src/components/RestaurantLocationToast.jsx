import {useEffect} from "react";
import {useTranslation} from "react-i18next";
import {Link} from "react-router-dom";
import "./styles/restaurant_toast.styles.css";

function RestaurantLocationToast({ restaurantId, dineIn }) {
    const { t } = useTranslation();

    const showToast = (querySelector) => {
        // eslint-disable-next-line no-undef
        const toast = bootstrap.Toast.getOrCreateInstance(document.querySelector(querySelector));
        if (!toast.isShown()) {
            toast.show();
        }
    };

    useEffect(() => {
        showToast(".restaurant_location_toast #icon-toast");
        showToast(".restaurant_location_toast #text-toast");

        // eslint-disable-next-line no-undef
        [...document.querySelectorAll('[data-bs-toggle="tooltip"]')].map(tooltipTriggerEl => new bootstrap.Tooltip(tooltipTriggerEl, {"trigger": "hover"}));
    }, []);

    return (
        <div className="restaurant_location_toast">
            <div className="toast-container p-3 bottom-0 end-0 position-fixed restaurant-menu-toasts d-flex flex-column align-items-end">
                <div className="toast" role="alert" id="text-toast" aria-live="assertive" aria-atomic="true" data-bs-autohide="false">
                    <div className="d-flex">
                        <div className="toast-body">
                            {
                                dineIn
                                    ?
                                    <>
                                        <span>{t("restaurant.toasts.dine_in.text")} {t("restaurant.toasts.click")} </span>
                                        <Link to={`/restaurants/${restaurantId}`}>{t("restaurant.toasts.here")}</Link>
                                        <span> {t("restaurant.toasts.dine_in.click")}</span>
                                    </>
                                    :
                                    <>
                                        <span>{t("restaurant.toasts.delivery.text")} {t("restaurant.toasts.click")} </span>
                                        <Link to={`/restaurants/${restaurantId}?qr=1`}>{t("restaurant.toasts.here")}</Link>
                                        <span> {t("restaurant.toasts.delivery.click")}</span>
                                    </>
                            }
                        </div>
                        <button type="button" className="btn-close me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                    </div>
                </div>
                <div className="toast text-bg-primary" id="icon-toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="false" onClick={() => showToast(".restaurant_location_toast #text-toast")}>
                    <div className="d-flex" data-bs-toggle="tooltip" data-bs-title={dineIn ? t("restaurant.toasts.dine_in.text") : t("restaurant.toasts.delivery.text")}>
                        <div className="toast-body">
                            <h5 className="m-0">
                                {dineIn ? <i className="bi bi-collection-fill default"></i> : <i className="bi bi-car-front-fill default"></i>}
                            </h5>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default RestaurantLocationToast;
