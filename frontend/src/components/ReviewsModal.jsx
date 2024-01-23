import {useCallback, useEffect, useState} from "react";
import { useTranslation } from "react-i18next";
import {useInfiniteQuery, useMutation, useQueryClient} from "@tanstack/react-query";
import {useApi} from "../hooks/useApi.js";
import {userReviewService} from "../hooks/services/useReviewService.js";
import ContentLoader from "react-content-loader";
import Rating from "./Rating.jsx";
import InternalOrderModal from "./InternalOrderModal.jsx";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {ReviewReplySchema} from "../data/validation.js";

function ReviewsModal({reviewsUrl, isEmployee, onClose}) {
    const { t, i18n } = useTranslation();
    const api = useApi();
    const reviewService = userReviewService(api);

    const [showInternalOrderModal, setShowInternalOrderModal] = useState(false);
    const [orderUrl, setOrderUrl] = useState(null);
    const closeModal = useCallback(() => onClose(), []);
    const [showReplyTextbox, setShowReplyTextbox] = useState({});
    const [showErrorAlert, setShowErrorAlert] = useState(false);

    useEffect(() => {
        // eslint-disable-next-line no-undef
        const modal = new bootstrap.Modal(document.querySelector(".reviews_modal .modal"));
        modal.show();
    }, []);
    useEffect(() => {
        document.querySelector(".reviews_modal .modal").addEventListener("hidden.bs.modal", closeModal);
    }, [closeModal]);

    const queryClient = useQueryClient();
    const {
        isPending,
        isError,
        data,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["reviews", reviewsUrl],
        queryFn: async ({ pageParam }) => (
            await reviewService.getReviews(
                reviewsUrl,
                {
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true
    });
    const replyReviewMutation = useMutation({
        mutationFn: async ({reviewUrl, reply}) => {
            await reviewService.replyReview(
                reviewUrl,
                reply
            );
        }
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    const handleShowOrderClicked = (orderUrl) => {
        document.querySelector(".reviews_modal .modal").removeEventListener("hidden.bs.modal", closeModal);
        // eslint-disable-next-line no-undef
        bootstrap.Modal.getOrCreateInstance(document.querySelector(".reviews_modal .modal")).hide();
        setOrderUrl(orderUrl);
        setShowInternalOrderModal(true);
    };

    const handleCloseOrderModal = () => {
        setShowInternalOrderModal(false);
        // eslint-disable-next-line no-undef
        bootstrap.Modal.getOrCreateInstance(document.querySelector(".reviews_modal .modal")).show();
        document.querySelector(".reviews_modal .modal").addEventListener("hidden.bs.modal", closeModal);
    };

    const handleReplyReview = (values, {setSubmitting, resetForm}) => {
        replyReviewMutation.mutate(
            {
                reviewUrl: values.reviewUrl,
                reply: values.reply
            },
            {
                onSuccess: async () => {
                    await queryClient.invalidateQueries({ queryKey: ["reviews"] });
                    setShowReplyTextbox({});
                    resetForm();
                },
                onError: () => setShowErrorAlert(true)
            }
        );
        setSubmitting(false);
    };

    return (
        <div className="reviews_modal">
            <div className="modal fade" tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered modal-dialog-scrollable">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h1 className="modal-title fs-5">{t("restaurant.reviews.title")}</h1>
                        </div>
                        <div className="modal-body">
                            {isError || isPending
                                ?
                                <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="466">
                                    <rect x="0" y="0" rx="0" ry="0" width="466" height="300"/>
                                    <rect x="324" y="0" rx="0" ry="0" width="466" height="380"/>
                                    <rect x="648" y="0" rx="0" ry="0" width="466" height="380"/>
                                </ContentLoader>
                                :
                                <>
                                    {showErrorAlert && <div className="alert alert-danger" role="alert">{t("restaurant.reviews.error_alert")}</div>}
                                    <div className="list-group list-group-flush">
                                        {data.pages.flatMap(page => page.content).map((review, i) => (
                                            <div className="list-group-item mt-3" key={i}>
                                                <b>{review.reviewerName}</b>
                                                <div className="d-flex gap-2 align-items-baseline my-2">
                                                    <Rating rating={review.rating} showText={false}/>
                                                    <small className="text-muted">{review.date.toLocaleString(i18n.language)}</small>
                                                </div>
                                                <p className="mb-2">
                                                    {review.comment}
                                                </p>
                                                {review.reply &&
                                                    <div className={`alert alert-light mt-2 ${isEmployee ? "mb-2" : "mb-4"}`} role="alert">
                                                        <b>{t("restaurant.reviews.reply")}</b>
                                                        <p className="m-0">
                                                            {review.reply}
                                                        </p>
                                                    </div>
                                                }
                                                {isEmployee && !review.reply && (review.orderUrl in showReplyTextbox) &&
                                                    <Formik
                                                        initialValues={{
                                                            reply: "",
                                                            reviewUrl: review.selfUrl
                                                        }}
                                                        validationSchema={ReviewReplySchema}
                                                        onSubmit={handleReplyReview}
                                                    >
                                                        {({isSubmitting, resetForm}) => (
                                                            <Form>
                                                                <div className={`alert alert-light mt-2 ${isEmployee ? "mb-2" : "mb-4"}`} role="alert">
                                                                    <Field as="textarea" className="form-control" name="reply" rows="3" placeholder={t("restaurant.reviews.reply_placeholder")}/>
                                                                    <ErrorMessage name="reply" className="form-error" component="div"/>
                                                                </div>
                                                                <div className="mb-4 d-flex justify-content-end">
                                                                    <button
                                                                        type="button"
                                                                        className="btn btn-link btn-sm link-secondary"
                                                                        onClick={() => handleShowOrderClicked(review.orderUrl)}
                                                                    >
                                                                        {t("restaurant.reviews.show_order_button")}
                                                                    </button>
                                                                    <button
                                                                        type="button"
                                                                        className="btn btn-secondary btn-sm me-1"
                                                                        onClick={() => {
                                                                            resetForm();
                                                                            setShowReplyTextbox({});
                                                                        }}
                                                                    >
                                                                        {t("restaurant.reviews.cancel_button")}
                                                                    </button>
                                                                    <button type="submit" className="btn btn-primary btn-sm" disabled={isSubmitting}>{t("restaurant.reviews.send_button")}</button>
                                                                </div>
                                                            </Form>
                                                        )}
                                                    </Formik>
                                                }
                                                {isEmployee && !(review.orderUrl in showReplyTextbox) &&
                                                    <div className="mb-4 d-flex justify-content-end">
                                                        <button
                                                            type="button"
                                                            className="btn btn-link btn-sm link-secondary"
                                                            onClick={() => handleShowOrderClicked(review.orderUrl)}
                                                        >
                                                            {t("restaurant.reviews.show_order_button")}
                                                        </button>
                                                        {
                                                            !review.reply &&
                                                            <button
                                                                type="button"
                                                                className="btn btn-primary btn-sm"
                                                                onClick={() => setShowReplyTextbox({[review.orderUrl]: true})}
                                                            >
                                                                {t("restaurant.reviews.reply_button")}
                                                            </button>
                                                        }
                                                    </div>
                                                }
                                            </div>
                                        ))}
                                        {isFetchingNextPage &&
                                            <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="466">
                                                <rect x="0" y="0" rx="0" ry="0" width="466" height="300"/>
                                                <rect x="324" y="0" rx="0" ry="0" width="466" height="380"/>
                                                <rect x="648" y="0" rx="0" ry="0" width="466" height="380"/>
                                            </ContentLoader>
                                        }
                                    </div>
                                    {hasNextPage &&
                                        <div className="d-flex justify-content-center align-items-center pt-2 pb-3">
                                            <button onClick={handleLoadMoreContent} className="btn btn-primary" disabled={isFetchingNextPage}>
                                                {
                                                    isFetchingNextPage
                                                        ?
                                                        <>
                                                            <span className="spinner-border spinner-border-sm mx-4" role="status"></span>
                                                            <span className="visually-hidden">Loading...</span>
                                                        </>
                                                        :
                                                        t("paging.load_more")
                                                }
                                            </button>
                                        </div>
                                    }
                                </>
                            }
                        </div>
                    </div>
                </div>
            </div>
            {showInternalOrderModal && <InternalOrderModal orderUrl={orderUrl} onClose={handleCloseOrderModal} showActions={false}/>}
        </div>
    );
}

export default ReviewsModal;
