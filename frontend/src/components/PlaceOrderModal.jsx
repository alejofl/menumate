import {useContext, useEffect, useRef, useState} from "react";
import { useTranslation } from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {GET_LANGUAGE_CODE, ORDER_TYPE, PRICE_DECIMAL_DIGITS} from "../utils.js";
import {useMutation, useQuery} from "@tanstack/react-query";
import {useUserService} from "../hooks/services/useUserService.js";
import Error from "../pages/Error.jsx";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {PlaceOrderSchema} from "../data/validation.js";
import ApiContext from "../contexts/ApiContext.jsx";
import {useOrderService} from "../hooks/services/useOrderService.js";

function PlaceOrderModal({restaurantId, maxTables, dineIn, dineInCompletionTime, deliveryCompletionTime, takeAwayCompletionTime, cart, onClose, onOrderCompleted}) {
    const { t, i18n } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);
    const orderService = useOrderService(api);

    const countdownLatch = useRef(false);

    const [orderType, setOrderType] = useState(dineIn ? ORDER_TYPE.DINE_IN : ORDER_TYPE.TAKE_AWAY);
    const [showErrorAlert, setShowErrorAlert] = useState(false);

    useEffect(() => {
        if (countdownLatch.current) {
            return;
        }

        // eslint-disable-next-line no-undef
        const modal = new bootstrap.Modal(document.querySelector(".place_order_modal .modal"));
        modal.show();

        countdownLatch.current = true;

        document.querySelector(".place_order_modal .modal").addEventListener("hidden.bs.modal", () => onClose());
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
    const placeOrderMutation = useMutation({
        mutationFn: async ({restaurantId, name, email, tableNumber, address, orderType, cart}) => {
            await orderService.placeOrder(
                apiContext.ordersUrl,
                restaurantId,
                name,
                email,
                tableNumber,
                address,
                orderType,
                cart,
                GET_LANGUAGE_CODE(i18n.language)
            );
        }
    });

    const handlePlaceOrder = (values, {setSubmitting}) => {
        placeOrderMutation.mutate(
            {
                restaurantId: restaurantId,
                name: values.name,
                email: values.email,
                tableNumber: values.tableNumber,
                address: values.address,
                orderType: orderType,
                cart: cart.map((product) => ({
                    productId: product.productId,
                    quantity: product.quantity,
                    comment: product.comments
                }))
            },
            {
                onSuccess: () => {
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getInstance(".place_order_modal .modal").hide();
                    onOrderCompleted();
                },
                onError: () => setShowErrorAlert(true)
            }
        );
        setSubmitting(false);
    };

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
    if (authContext.isAuthenticated && isPending) {
        return (
            <div className="place_order_modal">
                <div className="modal fade" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-header">
                                <h1 className="modal-title fs-5">{t("restaurant.checkout.title")}</h1>
                            </div>
                            <div className="modal-body d-flex justify-content-center">
                                <div className="spinner-border text-primary" role="status">
                                    <span className="visually-hidden">Loading...</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
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
                        <Formik
                            initialValues={{
                                name: authContext.isAuthenticated ? authContext.name : "",
                                email: authContext.isAuthenticated && !!userData ? userData.email : "",
                                tableNumber: "",
                                address: ""
                            }}
                            validationSchema={() => PlaceOrderSchema(orderType, maxTables)}
                            onSubmit={handlePlaceOrder}
                        >
                            {({isSubmitting}) => (
                                <Form>
                                    <div className="modal-body">
                                        {
                                            showErrorAlert &&
                                            <div className="alert alert-danger fade show" role="alert">{t("restaurant.checkout.error")}</div>
                                        }
                                        {
                                            authContext.isAuthenticated
                                                ?
                                                <>
                                                    <h5 className="mb-4">{t("restaurant.checkout.welcome_back", {name: authContext.name})}</h5>
                                                    <Field type="hidden" name="name" className="form-control" value={authContext.name}/>
                                                    <Field type="hidden" name="email" className="form-control" value={userData.email}/>
                                                </>
                                                :
                                                <>
                                                    <div className="mb-3">
                                                        <label htmlFor="name" className="form-label">{t("restaurant.checkout.name")}</label>
                                                        <Field type="text" name="name" className="form-control" id="name"/>
                                                        <ErrorMessage name="name" className="form-error" component="div"/>
                                                    </div>
                                                    <div className="mb-3">
                                                        <label htmlFor="email" className="form-label">{t("restaurant.checkout.email")}</label>
                                                        <Field type="email" name="email" className="form-control" id="email"/>
                                                        <ErrorMessage name="email" className="form-error" component="div"/>
                                                    </div>
                                                </>
                                        }
                                        {
                                            dineIn
                                                ?
                                                <>
                                                    <div className="mb-3">
                                                        <label htmlFor="tableNumber" className="form-label">{t("restaurant.checkout.table_number")}</label>
                                                        <Field type="number" name="tableNumber" className="form-control" id="tableNumber"/>
                                                        <ErrorMessage name="tableNumber" className="form-error" component="div"/>
                                                    </div>
                                                    {dineInCompletionTime &&
                                                        <p dangerouslySetInnerHTML={{__html: t("restaurant.checkout.eta", {eta: dineInCompletionTime})}}></p>}
                                                </>
                                                :
                                                <>
                                                    <nav>
                                                        <div className="nav nav-pills nav-fill secondary-nav mb-3" role="tablist">
                                                            <button className="nav-link active" data-bs-toggle="tab" data-bs-target="#place-order-takeaway" type="button" role="tab" onClick={() => setOrderType(ORDER_TYPE.TAKE_AWAY)}>
                                                                {t("restaurant.checkout.take_away")}
                                                            </button>
                                                            <button className="nav-link" data-bs-toggle="tab" data-bs-target="#place-order-delivery" type="button" role="tab" onClick={() => setOrderType(ORDER_TYPE.DELIVERY)}>
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
                                                                <Field type="text" name="address" className="form-control" id="address" list="addresses-list"/>
                                                                <ErrorMessage name="address" className="form-error" component="div"/>
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
                                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                                            {t("restaurant.checkout.place_order", {price: cart.reduce((acc, cur) => acc + cur.price * cur.quantity, 0).toFixed(PRICE_DECIMAL_DIGITS)})}
                                        </button>
                                    </div>
                                </Form>
                            )}
                        </Formik>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default PlaceOrderModal;
