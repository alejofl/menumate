import {useContext, useEffect} from "react";
import { useTranslation } from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {PRICE_DECIMAL_DIGITS} from "../utils.js";
import {useQuery} from "@tanstack/react-query";
import {useUserService} from "../hooks/services/useUserService.js";
import Error from "../pages/Error.jsx";

function PlaceOrderModal({dineIn, dineInCompletionTime, deliveryCompletionTime, takeAwayCompletionTime, cart, onClose}) {
    const { t } = useTranslation();
    const api = useApi();
    const userService = useUserService(api);
    const authContext = useContext(AuthContext);

    useEffect(() => {
        // eslint-disable-next-line no-undef
        const modal = new bootstrap.Modal(document.querySelector(".place_order_modal .modal"));
        modal.show();

        document.addEventListener("hidden.bs.modal", () => onClose());

        return () => {
            document.removeEventListener("hidden.bs.modal", null);
        };
    }, [onClose]);

    const { isError: userIsError, data: userData, error: userError} = useQuery({
        queryKey: ["user", authContext.selfUrl],
        queryFn: async () => (
            await userService.getUser(authContext.selfUrl)
        ),
        enabled: authContext.isAuthenticated
    });
    const { isPending, isError: addressesIsError, data: addresses, error: addressesError} = useQuery({
        queryKey: ["user", authContext.selfUrl, "addresses"],
        queryFn: async () => (
            await userService.getAddresses(userData.addressesUrl)
        ),
        enabled: !!userData
    });

    if (userIsError) {
        return (
            <>
                <Error errorNumber={userError.response.status}/>
            </>
        );
    } else if (addressesIsError) {
        return (
            <>
                <Error errorNumber={addressesError.response.status}/>
            </>
        );
    }

    return (
        <div className="place_order_modal">
            <div className="modal fade" tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{t("restaurant.checkout.title")}</h1>
                        </div>
                        <div className="modal-body">
                            {
                                authContext.isAuthenticated
                                    ?
                                    <h5 className="mb-4">{t("restaurant.checkout.welcome_back", {name: authContext.name})}</h5>
                                    :
                                    <>
                                        <div className="mb-3">
                                            <label htmlFor="name" className="form-label">{t("restaurant.checkout.name")}</label>
                                            <input type="text" name="name" className="form-control" id="name"/>
                                            {/* <ErrorMessage name="name" className="form-error" component="div"/> */}
                                        </div>
                                        <div className="mb-3">
                                            <label htmlFor="email" className="form-label">{t("restaurant.checkout.email")}</label>
                                            <input type="email" name="email" className="form-control" id="email"/>
                                            {/* <ErrorMessage name="email" className="form-error" component="div"/> */}
                                        </div>
                                    </>
                            }
                            {
                                dineIn
                                    ?
                                    <>
                                        <div className="mb-3">
                                            <label htmlFor="tableNumber" className="form-label">{t("restaurant.checkout.table_number")}</label>
                                            <input type="number" name="tableNumber" className="form-control" id="tableNumber"/>
                                            {/* <ErrorMessage name="tableNumber" className="form-error" component="div"/> */}
                                        </div>
                                        {dineInCompletionTime &&
                                            <p dangerouslySetInnerHTML={{__html: t("restaurant.checkout.eta", {eta: dineInCompletionTime})}}></p>}
                                    </>
                                    :
                                    <>
                                        <nav>
                                            <div className="nav nav-pills nav-fill secondary-nav mb-3" role="tablist">
                                                <button className="nav-link active" data-bs-toggle="tab" data-bs-target="#place-order-takeaway" type="button" role="tab">
                                                    {t("restaurant.checkout.take_away")}
                                                </button>
                                                <button className="nav-link" data-bs-toggle="tab" data-bs-target="#place-order-delivery" type="button" role="tab">
                                                    {t("restaurant.checkout.delivery")}
                                                </button>
                                            </div>
                                        </nav>
                                        <div className="tab-content">
                                            <div className="tab-pane active show" id="place-order-takeaway" role="tabpanel" tabIndex="0">
                                                {takeAwayCompletionTime &&
                                                    <p dangerouslySetInnerHTML={{__html: t("restaurant.checkout.eta", {eta: takeAwayCompletionTime})}}></p>}
                                            </div>
                                            <div className="tab-pane" id="place-order-delivery" role="tabpanel" tabIndex="0">
                                                <div className="mb-3">
                                                    <label htmlFor="address" className="form-label">{t("restaurant.checkout.delivery_address")}</label>
                                                    <input type="text" name="address" className="form-control" id="address" list="addresses-list"/>
                                                    {/* <ErrorMessage name="address" className="form-error" component="div"/> */}
                                                    <datalist id="addresses-list">
                                                        {!isPending && addresses.map((address, i) => (
                                                            <option key={i} value={address.address}>{address.name || address.address}</option>
                                                        ))}
                                                    </datalist>
                                                    {authContext.isAuthenticated &&
                                                        <div className="form-text">{t("restaurant.checkout.address_disclaimer")}</div>}
                                                </div>
                                                {deliveryCompletionTime &&
                                                    <p dangerouslySetInnerHTML={{__html: t("restaurant.checkout.eta", {eta: deliveryCompletionTime})}}></p>}
                                            </div>
                                        </div>
                                    </>
                            }
                            <div className="d-flex align-items-center justify-content-center">
                                <small>{t("restaurant.checkout.payment_method")}</small>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="submit" className="btn btn-primary">
                                {t("restaurant.checkout.place_order", {price: cart.reduce((acc, cur) => acc + cur.price * cur.quantity, 0).toFixed(PRICE_DECIMAL_DIGITS)})}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PlaceOrderModal;
