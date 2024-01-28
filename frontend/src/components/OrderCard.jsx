/* eslint-disable no-magic-numbers */
import "./styles/order_card.styles.css";
import {useTranslation} from "react-i18next";
import {useMutation, useQueries, useQuery, useQueryClient} from "@tanstack/react-query";
import {useOrderService} from "../hooks/services/useOrderService.js";
import {useApi} from "../hooks/useApi.js";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import ContentLoader from "react-content-loader";
import {NOT_FOUND_STATUS_CODE, ORDER_STATUS, ORDER_TYPE, PRICE_DECIMAL_DIGITS} from "../utils.js";
import {useNavigate} from "react-router-dom";
import Rating from "./Rating.jsx";
import {useReviewService} from "../hooks/services/useReviewService.js";
import {useContext, useEffect, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {ReviewSchema} from "../data/validation.js";

function OrderCard({orderUrl, showCard = true, onError}) {
    const { t, i18n } = useTranslation();
    const apiContext = useContext(ApiContext);
    const api = useApi();
    const orderService = useOrderService(api);
    const restaurantService = useRestaurantService(api);
    const reviewService = useReviewService(api);

    const navigate = useNavigate();

    const { isError: orderIsError, data: order, error: orderError} = useQuery({
        queryKey: ["order", orderUrl],
        queryFn: async () => (
            await orderService.getOrder(orderUrl)
        )
    });
    const { isPending: restaurantIsPending, isError: restaurantIsError, data: restaurant, error: restaurantError } = useQuery({
        queryKey: ["order", orderUrl, "restaurant"],
        queryFn: async () => (
            await restaurantService.getRestaurant(order.restaurantUrl, false)
        ),
        enabled: !!order
    });
    const { isPending: orderItemsIsPending, isError: orderItemsIsError, data: orderItems, error: orderItemsError} = useQuery({
        queryKey: ["order", orderUrl, "items"],
        queryFn: async () => (
            await orderService.getOrderItems(order.itemsUrl)
        ),
        enabled: !!order
    });
    const products = useQueries({
        queries: orderItems
            ?
            orderItems.sort((a, b) => a.lineNumber - b.lineNumber).map(item => {
                return {
                    queryKey: ["order", orderUrl, "items", item.lineNumber, "product"],
                    queryFn: async () => (
                        await restaurantService.getProduct(item.productUrl)
                    )
                };
            })
            :
            []
    });
    const { isPending: reviewIsPending, isError: reviewIsError, data: review, error: reviewError } = useQuery({
        queryKey: ["order", orderUrl, "review"],
        queryFn: async () => (
            await reviewService.getReview(apiContext.reviewsUriTemplate.fill({orderId: order.orderId}))
        ),
        enabled: !!order
    });

    const [showReviewErrorAlert, setShowReviewErrorAlert] = useState(false);
    const [rating, setRating] = useState(0);
    const [hoverRating, setHoverRating] = useState(0);

    const makeReviewMutation = useMutation({
        mutationFn: async ({rating, comment}) => {
            await reviewService.makeReview(
                apiContext.reviewsUrl,
                order.orderId,
                rating,
                comment
            );
        }
    });

    const queryClient = useQueryClient();

    const handleSendReview = (values, {setSubmitting, resetForm}) => {
        makeReviewMutation.mutate(
            {
                rating: rating,
                comment: values.comment
            },
            {
                onSuccess: () => {
                    queryClient.invalidateQueries({queryKey: ["order", orderUrl, "review"]});
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector(`#review-${order.orderId}-modal`)).hide();
                    // eslint-disable-next-line no-undef
                    bootstrap.Modal.getOrCreateInstance(document.querySelector(`#order-${order.orderId}-details`)).show();
                    setHoverRating(0);
                    setRating(0);
                    setShowReviewErrorAlert(false);
                    resetForm();
                },
                onError: () => setShowReviewErrorAlert(true)
            }
        );
        setSubmitting(false);
    };

    useEffect(() => {
        if (
            !(orderIsError || restaurantIsError || orderItemsIsError || products.some(product => product.isError)) &&
            !(restaurantIsPending || orderItemsIsPending || products.some(product => product.isPending) || reviewIsPending) &&
            !showCard
        ) {
            // eslint-disable-next-line no-undef
            bootstrap.Modal.getOrCreateInstance(document.querySelector(`#order-${order.orderId}-details`)).show();
        }
    }, [restaurantIsPending, orderItemsIsPending, products, reviewIsPending]);

    if (orderIsError) {
        onError(orderError.response.status);
    } else if (restaurantIsError) {
        onError(restaurantError.response.status);
    } else if (orderItemsIsError) {
        onError(orderItemsError.response.status);
    } else if (products.some(product => product.isError)) {
        onError(500);
    } else if (restaurantIsPending || orderItemsIsPending || products.some(product => product.isPending) || reviewIsPending) {
        return (
            <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="15rem" height="20rem">
                <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
            </ContentLoader>
        );
    }
    return (
        <>
            {
                showCard &&
                <div className="order_card">
                    <div className="clickable-object" data-bs-toggle="modal" data-bs-target={`#order-${order.orderId}-details`}>
                        <div className="card">
                            <div className="card-body d-flex align-items-center">
                                <img src={restaurant.logoUrl} alt={restaurant.name}/>
                                <div>
                                    <small className="text-muted">
                                        {t("order.title", {id: order.orderId})}
                                    </small>
                                    <h5 className="card-title mb-0">
                                        {restaurant.name}
                                    </h5>
                                </div>
                            </div>
                            <ul className="list-group list-group-flush">
                                <li className="list-group-item">
                                    <i className="bi bi-card-list me-2"></i>
                                    {t(`order.${order.orderType}`)}
                                </li>
                                <li className="list-group-item">
                                    <i className="bi bi-calendar-event me-2"></i>
                                    {order.dateOrdered.toLocaleString(i18n.language)}
                                </li>
                                <li className="list-group-item">
                                    <i className="bi bi-cart me-2"></i>
                                    {t("order.product_quantity", {count: orderItems.reduce((total, item) => total + item.quantity, 0)})}
                                </li>
                                <li className="list-group-item">
                                    <i className="bi bi-cash-stack me-2"></i>
                                    ${orderItems.reduce((total, item, i) => total + item.quantity * products[i].data.price, 0).toFixed(PRICE_DECIMAL_DIGITS)}
                                </li>
                            </ul>
                            <div className="card-footer">
                                {t(`order.status.order_${order.status}`)}
                            </div>
                        </div>
                    </div>
                </div>
            }

            <div className="modal fade order_details_modal" id={`order-${order.orderId}-details`} tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                    <div className="modal-content">
                        <img src={restaurant.portrait1Url} alt={restaurant.name}/>
                        <div className="order-information">
                            <div className="order-details-restaurant">
                                <img src={restaurant.logoUrl} alt={restaurant.name}/>
                                <div>
                                    <small className="text-muted">{t("order.title", {id: order.orderId})}</small>
                                    <h3 className="card-title mb-0">{restaurant.name}</h3>
                                    <hr/>
                                    <i className="bi bi-calendar-event me-2"></i>
                                    {order.dateOrdered.toLocaleString(i18n.language)}
                                </div>
                            </div>
                            <div className={`alert alert-${ORDER_STATUS[order.status.toUpperCase()].bgColor} d-flex flex-column align-items-center mb-0`} role="alert">
                                <div className="pb-2">
                                    {t(`order.status.order_${order.status}`)}
                                </div>
                                <div className="progress w-100" role="progressbar">
                                    <div className={`progress-bar bg-${ORDER_STATUS[order.status.toUpperCase()].color}`} style={{width: ORDER_STATUS[order.status.toUpperCase()].progress}}></div>
                                </div>
                            </div>
                        </div>
                        <div className="modal-body">
                            <div className="card-body">
                                <div>
                                    <h4>{t("order.details")}</h4>
                                    <table className="table">
                                        <thead className="table-light">
                                            <tr>
                                                <th scope="col" className="text-start">#</th>
                                                <th scope="col" className="text-start">{t("order.product")}</th>
                                                <th scope="col" className="text-center">{t("order.quantity")}</th>
                                                <th scope="col" className="text-end">{t("order.price")}</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {orderItems.sort((a, b) => a.lineNumber - b.lineNumber).map((item, i) => {
                                                return (
                                                    <tr key={i}>
                                                        <th className="text-start" scope="row">{item.lineNumber}</th>
                                                        <td className="text-start">{products[i].data.name}</td>
                                                        <td className="text-center">{item.quantity}</td>
                                                        <td className="text-end">${products[i].data.price}</td>
                                                    </tr>
                                                );
                                            })}
                                        </tbody>
                                        <tfoot>
                                            <tr>
                                                <th colSpan="3">{t("order.total_price")}</th>
                                                <th className="text-end">${orderItems.reduce((total, item, i) => total + item.quantity * products[i].data.price, 0).toFixed(PRICE_DECIMAL_DIGITS)}</th>
                                            </tr>
                                        </tfoot>
                                    </table>
                                    <ul className="list-group list-group-flush">
                                        <li className="list-group-item d-flex align-items-center">
                                            <i className="bi bi-card-list me-3"></i>
                                            <div>
                                                <small className="text-muted">{t("order.order_type")}</small>
                                                <p className="mb-0">{t(`order.${order.orderType}`)}</p>
                                            </div>
                                        </li>
                                        {
                                            order.orderType === ORDER_TYPE.DINE_IN &&
                                            <li className="list-group-item d-flex align-items-center">
                                                <i className="bi bi-hash me-3"></i>
                                                <div>
                                                    <small className="text-muted">{t("order.table_number")}</small>
                                                    <p className="mb-0" id="order-details-table-number">{order.tableNumber}</p>
                                                </div>
                                            </li>
                                        }
                                        {
                                            order.orderType === ORDER_TYPE.DELIVERY &&
                                            <li className="list-group-item d-flex align-items-center">
                                                <i className="bi bi-geo-alt me-3"></i>
                                                <div>
                                                    <small className="text-muted">{t("order.address")}</small>
                                                    <p className="mb-0" id="order-details-address">{order.address}</p>
                                                </div>
                                            </li>
                                        }
                                    </ul>
                                    {!reviewIsError &&
                                        <div className="alert alert-light my-2" role="alert">
                                            <b>{t("order.your_review")}</b>
                                            <div className="d-flex gap-2 align-items-baseline my-1">
                                                <Rating rating={review.rating} showText={false}/>
                                                <small className="text-muted">{review.date.toLocaleString(i18n.language)}</small>
                                            </div>
                                            <p className="m-0">
                                                {review.comment}
                                            </p>
                                            {review.reply &&
                                                <div className="alert alert-light my-2" role="alert">
                                                    <b>{t("restaurant.reviews.reply")}</b>
                                                    <p className="m-0">
                                                        {review.reply}
                                                    </p>
                                                </div>
                                            }
                                        </div>
                                    }
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            {
                                order.status === ORDER_STATUS.DELIVERED.text && reviewIsError && reviewError.response.status === NOT_FOUND_STATUS_CODE &&
                                <button className="btn btn-secondary" data-bs-toggle="modal" data-bs-target={`#review-${order.orderId}-modal`}>
                                    {t("order.review")}
                                </button>
                            }
                            <button className="btn btn-primary" onClick={() => navigate(`/restaurants/${restaurant.restaurantId}`)} data-bs-dismiss="modal">
                                {t("order.order_again")}
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div className="modal fade order_review_modal" id={`review-${order.orderId}-modal`} tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{t("order.review")}</h1>
                            <button type="button" className="btn-close" data-bs-dismiss="modal"></button>
                        </div>
                        <Formik
                            initialValues={{
                                comment: ""
                            }}
                            validationSchema={ReviewSchema}
                            onSubmit={handleSendReview}
                        >
                            {({isSubmitting, resetForm}) => (
                                <Form>
                                    <div className="modal-body">
                                        {showReviewErrorAlert && <div className="alert alert-danger" role="alert">{t("order.error")}</div>}
                                        <div className="mb-3">
                                            <label className="form-label mb-0">{t("order.rating")}</label>
                                            <div className="rating">
                                                <div className="small-ratings">
                                                    <i
                                                        className={`bi bi-star-fill default ${rating >= 1 || hoverRating >= 1 ? "rating-color" : ""}`}
                                                        onClick={() => setRating(1)}
                                                        onMouseOver={() => setHoverRating(1)}
                                                        onMouseOut={() => setHoverRating(0)}
                                                    >
                                                    </i>
                                                    <i
                                                        className={`bi bi-star-fill default ${rating >= 2 || hoverRating >= 2 ? "rating-color" : ""}`}
                                                        onClick={() => setRating(2)}
                                                        onMouseOver={() => setHoverRating(2)}
                                                        onMouseOut={() => setHoverRating(0)}
                                                    >
                                                    </i>
                                                    <i
                                                        className={`bi bi-star-fill default ${rating >= 3 || hoverRating >= 3 ? "rating-color" : ""}`}
                                                        onClick={() => setRating(3)}
                                                        onMouseOver={() => setHoverRating(3)}
                                                        onMouseOut={() => setHoverRating(0)}
                                                    >
                                                    </i>
                                                    <i
                                                        className={`bi bi-star-fill default ${rating >= 4 || hoverRating >= 4 ? "rating-color" : ""}`}
                                                        onClick={() => setRating(4)}
                                                        onMouseOver={() => setHoverRating(4)}
                                                        onMouseOut={() => setHoverRating(0)}
                                                    >
                                                    </i>
                                                    <i
                                                        className={`bi bi-star-fill default ${rating >= 5 || hoverRating >= 5 ? "rating-color" : ""}`}
                                                        onClick={() => setRating(5)}
                                                        onMouseOver={() => setHoverRating(5)}
                                                        onMouseOut={() => setHoverRating(0)}
                                                    >
                                                    </i>
                                                </div>
                                            </div>
                                        </div>
                                        <div>
                                            <label htmlFor="comment" className="form-label">{t("order.experience")}</label>
                                            <Field as="textarea" className="form-control" name="comment" id="comment" rows="3"/>
                                            <ErrorMessage name="comment" component="div" className="form-error"/>
                                        </div>
                                    </div>
                                    <div className="modal-footer">
                                        <button
                                            type="button"
                                            className="btn btn-secondary"
                                            data-bs-toggle="modal"
                                            data-bs-target={`#order-${order.orderId}-details`}
                                            onClick={() => {
                                                setShowReviewErrorAlert(false);
                                                setHoverRating(0);
                                                setRating(0);
                                                resetForm();
                                            }}
                                        >
                                            {t("order.cancel")}
                                        </button>
                                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("order.send_review")}</button>
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

export default OrderCard;
