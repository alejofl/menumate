import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import "./styles/user_profile.styles.css";
import {useApi} from "../hooks/useApi.js";
import {useContext, useRef, useState} from "react";
import {useUserService} from "../hooks/services/useUserService.js";
import {useInfiniteQuery, useMutation, useQueries, useQuery, useQueryClient} from "@tanstack/react-query";
import AuthContext from "../contexts/AuthContext.jsx";
import {useSearchParams} from "react-router-dom";
import {useOrderService} from "../hooks/services/useOrderService.js";
import ContentLoader from "react-content-loader";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import ErrorModal from "../components/ErrorModal.jsx";
import Error from "./Error.jsx";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {RegisterAddressSchema} from "../data/validation.js";
import {BAD_REQUEST_STATUS_CODE, METHOD_NOT_ALLOWED_STATUS_CODE} from "../utils.js";
import Rating from "../components/Rating.jsx";
import {useReviewService} from "../hooks/services/useReviewService.js";
import { Modal } from "bootstrap";

function UserProfile() {
    const DEFAULT_ORDER_COUNT = 20;
    const { t, i18n } = useTranslation();
    const api = useApi();
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);
    const reviewService = useReviewService(api);
    const orderService = useOrderService(api);
    const restaurantService = useRestaurantService(api);

    const [queryParams] = useSearchParams();
    const [query] = useState({
        ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
    });

    const [currentDeleteAddressModal, setCurrentDeleteAddressModal] = useState("");
    const [currentNameAddress, setCurrentNameAddress] = useState("");
    const [currentAddressAddress, setCurrentAddressAddress] = useState("");
    const [currentUrlAddress, setCurrentUrlAddress] = useState("");
    const [addressRepeated, setAddressRepeated] = useState(false);
    const [editOperation, setEditOperation] = useState(false);

    const editAddressFormRef = useRef();

    const queryClient = useQueryClient();

    const {
        isPending : userIsPending,
        isError : userIsError,
        error : userError,
        data : user
    } = useQuery({
        queryKey: ["user", authContext.selfUrl],
        queryFn: async () => (
            await userService.getUser(
                authContext.selfUrl
            )
        )
    });

    const {
        isPending : addressesIsPending,
        isError : addressesIsError,
        error : addressesError,
        data: addresses
    } = useQuery({
        queryKey: ["user", authContext.selfUrl, "addresses"],
        queryFn: async () => (
            await userService.getAddresses(
                user.addressesUrl
            )
        ),
        enabled: !!user
    });

    const {
        isLoading : reviewsIsLoading,
        isError : reviewsIsError,
        data: reviews,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery( {
        queryKey: ["user", authContext.selfUrl, "reviews"],
        queryFn: async ({ pageParam }) => (
            await reviewService.getReviews(
                user.reviewsUrl,
                {
                    ...query,
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: !!user
    });

    const orders = useQueries({
        queries: reviews
            ?
            reviews.pages.flatMap(page => page.content).map(review => {
                return {
                    queryKey: ["order", review.orderUrl],
                    queryFn: async () => (
                        await orderService.getOrder(review.orderUrl)
                    )
                };
            })
            :
            [],
        enabled: !!user
    });

    const restaurants = useQueries({
        queries: orders.every(query => query.isSuccess)
            ?
            orders.map(order => {
                return {
                    queryKey: ["restaurant", order.data.restaurantUrl],
                    queryFn: async () => {
                        return await restaurantService.getRestaurant(order.data.restaurantUrl);
                    }};
            })
            : []
    });

    const registerAddressMutation = useMutation({
        mutationFn: async ({name, address}) => {
            await userService.registerAddress(
                user.addressesUrl,
                name,
                address
            );
        }
    });

    const handleRegisterAddress = (values, {setSubmitting}) => {
        registerAddressMutation.mutate(
            {
                name: values.name,
                address: values.address
            },
            {
                onSuccess: () => {
                    queryClient.invalidateQueries({queryKey: ["user", authContext.selfUrl, "addresses"]});
                    Modal.getOrCreateInstance(document.querySelector("#editAddressModal")).hide();
                },
                onError: (error) => {
                    if (error.response.status === BAD_REQUEST_STATUS_CODE) {
                        setAddressRepeated(true);
                    }
                }
            }
        );
        setSubmitting(false);
    };

    const handleOpenRegisterAddressModal = () => {
        setEditOperation(false);
        setCurrentNameAddress("");
        setCurrentAddressAddress("");
        setCurrentUrlAddress("");
        const modal = Modal.getOrCreateInstance(document.querySelector("#editAddressModal"));
        document.querySelector("#editAddressModal").addEventListener("hidden.bs.modal", handleModalHidden);
        modal.show();
    };

    const updateAddressMutation = useMutation({
        mutationFn: async ({name, address}) => {
            await userService.updateAddress(
                currentUrlAddress,
                name,
                address
            );
        }
    });

    const handleUpdateAddress = (values, {setSubmitting}) => {
        updateAddressMutation.mutate(
            {
                name: values.name,
                address: values.address
            },
            {
                onSuccess: () => {
                    queryClient.invalidateQueries({queryKey: ["user", authContext.selfUrl, "addresses"]});
                    Modal.getOrCreateInstance(document.querySelector("#editAddressModal")).hide();
                },
                onError: (error) => {
                    if (error.response.status === METHOD_NOT_ALLOWED_STATUS_CODE) {
                        setAddressRepeated(true);
                    }
                }
            }
        );
        setSubmitting(false);
    };

    const handleOpenUpdateAddressModal = (address) => {
        setEditOperation(true);
        setCurrentUrlAddress(address.selfUrl);
        setCurrentNameAddress(address.name || "");
        setCurrentAddressAddress(address.address || "");

        const modal = Modal.getOrCreateInstance(document.querySelector("#editAddressModal"));
        document.querySelector("#editAddressModal").addEventListener("hidden.bs.modal", handleModalHidden);
        modal.show();
    };

    const handleModalHidden = () => {
        editAddressFormRef.current?.resetForm();
        setAddressRepeated(false);
    };

    const deleteAddressMutation = useMutation({
        mutationFn: async ({url}) => {
            await userService.deleteAddress(url);
        }
    });

    const handleDeleteAddress = () => {
        deleteAddressMutation.mutate(
            {
                url: currentDeleteAddressModal
            },
            {
                onSuccess: () => queryClient.invalidateQueries({queryKey: ["user", authContext.selfUrl, "addresses"]})
            }
        );
    };

    const handleOpenDeleteAddressModal = (url) => {
        setCurrentDeleteAddressModal(url);
        const modal = new Modal(document.querySelector(".deleteAddressModal .modal"));
        modal.show();
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
    } else if (orders.some(product => product.isError) || restaurants.some(product => product.isError)) {
        return (
            <>
                <Error errorNumber="500"/>
            </>
        );
    } else if (userIsPending || addressesIsPending) {
        return (
            <>
                <Page title={t("titles.loading")}>
                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="100%" height="40rem">
                        <rect x="2%" y="50" rx="0" ry="0" width="46%" height="100%"/>
                        <rect x="52%" y="50" rx="0" ry="0" width="46%" height="20%"/>
                        <rect x="52%" y="200" rx="0" ry="0" width="46%" height="20%"/>
                        <rect x="52%" y="350" rx="0" ry="0" width="46%" height="20%"/>
                        <rect x="52%" y="500" rx="0" ry="0" width="46%" height="20%"/>
                    </ContentLoader>
                </Page>
            </>
        );
    }

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    return (
        <>
            <Page title={t("titles.user_profile")} className="user_profile">
                <div className="profile-container">
                    <div className="user-info-container">
                        <div className="card">
                            <div className="card-body p-4">
                                <h3 className="card-title">{t("user_profile.profile")}</h3>
                                <label htmlFor="name"
                                    className="form-label mt-2">{t("user_profile.name_label")}</label>
                                <input type="email" className="form-control" id="name" readOnly value={user?.name}/>
                                <label htmlFor="email"
                                    className="form-label mt-2">{t("user_profile.email_label")}</label>
                                <input type="email" className="form-control" id="email" value={user?.email} readOnly/>
                                <hr/>
                                <h4 className="card-title">{t("user_profile.addresses")}</h4>
                                <ul className="list-group list-group-flush mb-2">
                                    {addresses?.map((address, i) => (
                                        <li key={i}
                                            className="list-group-item d-flex align-items-center justify-content-between px-0"
                                        >
                                            <div className="d-flex align-items-center gap-2">
                                                <i className="bi bi-geo-alt"></i>
                                                <div>
                                                    {
                                                        address.name &&
                                                        <small className="text-muted">{address.name}</small>
                                                    }
                                                    <p className="mb-0">{address.address}</p>
                                                </div>
                                            </div>
                                            <div className="d-flex gap-2">
                                                {
                                                    !address.name
                                                    &&
                                                    <a
                                                        className="add-address-modal-button"
                                                        type="button"
                                                        onClick={() => handleOpenUpdateAddressModal(address)}
                                                    >
                                                        <i className="bi bi-save-fill text-success right-button"></i>
                                                    </a>
                                                }
                                                <a
                                                    className="delete-address-modal-button"
                                                    type="button"
                                                    onClick={() => handleOpenDeleteAddressModal(address.selfUrl)}
                                                >
                                                    <i className="bi bi-trash-fill text-danger right-button"></i>
                                                </a>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                                <div className="d-flex mt-2">
                                    <button
                                        className="btn btn-primary flex-grow-1"
                                        type="button"
                                        onClick={handleOpenRegisterAddressModal}
                                    >
                                        {t("user_profile.add_address")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="reviews-container">
                        <h3 className="page-title">{t("user_profile.reviews")}</h3>
                        {
                            reviewsIsLoading || orders.some(order => order.isPending) || restaurants.some(restaurant => restaurant.isPending)
                                ?
                                new Array(DEFAULT_ORDER_COUNT).fill("").map((_, i) => {
                                    return (
                                        <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="100%" height="110" key={i}>
                                            <rect x="0%" y="0" rx="0" ry="0" width="96%" height="100"/>
                                        </ContentLoader>
                                    );
                                })
                                :
                                (reviews?.pages[0]?.content.length || 0) === 0
                                    ?
                                    <div className="empty-results">
                                        <h1><i className="bi bi-slash-circle default"></i></h1>
                                        <p>{t("restaurants.no_results")}</p>
                                    </div>
                                    :
                                    <>
                                        {orders.every(query => query.isSuccess) && reviews.pages.flatMap(page => page.content).map(review => (
                                            <div className="card mb-2" key={review.orderId}>
                                                <div className="card-header">
                                                    <div className="my-review-card-header">
                                                        <div className="my-review-card-header-info">
                                                            <b>{
                                                                restaurants.find(restaurant =>
                                                                    restaurant.data?.selfUrl ===
                                                                    orders.find(order => order.data?.orderId === review.orderId).data?.restaurantUrl
                                                                ).data?.name
                                                            }</b>
                                                            <small className="text-muted">{review.date.toLocaleString(i18n.language)}</small>
                                                        </div>
                                                        <Rating rating={review.rating} showText={false}/>
                                                    </div>
                                                </div>
                                                <div className="card-body">
                                                    <p>{review.comment}</p>
                                                </div>
                                            </div>
                                        ))}
                                    </>
                        }
                        {
                            isFetchingNextPage &&
                            new Array(query.size || DEFAULT_ORDER_COUNT).fill("").map((_, i) => {
                                return (
                                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="100%"
                                        height="40rem" key={i}>
                                        <rect x="0%" y="0" rx="0" ry="0" width="96%" height="100"/>
                                    </ContentLoader>
                                );
                            })
                        }
                        {
                            hasNextPage &&
                            <div className="d-flex justify-content-center align-items-center pt-2 pb-3">
                                <button onClick={handleLoadMoreContent} className="btn btn-primary"
                                    disabled={isFetchingNextPage}>
                                    {
                                        isFetchingNextPage
                                            ?
                                            <>
                                                <span className="spinner-border spinner-border-sm mx-4"
                                                    role="status"></span>
                                                <span className="visually-hidden">Loading...</span>
                                            </>
                                            :
                                            t("paging.load_more")
                                    }
                                </button>
                            </div>
                        }
                        {reviewsIsError && <ErrorModal/>}
                    </div>
                </div>
            </Page>

            <div className="deleteAddressModal">
                <div className="modal fade" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-body">
                                <h1 className="modal-title fs-5">
                                    {t("user_profile.delete_address_title")}
                                </h1>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary" data-bs-dismiss="modal">
                                    {t("paging.no")}
                                </button>
                                <button className="btn btn-danger" type="submit" onClick={handleDeleteAddress} data-bs-dismiss="modal">{t("paging.yes")}</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="modal fade" id="editAddressModal" tabIndex="-1">
                <div className="modal-dialog modal-dialog-centered">
                    <div className="modal-content">
                        <div className="modal-header"><h1 className="modal-title fs-5">
                            {t("user_profile.add_address")}
                        </h1>
                        </div>
                        <div className="modal-body">
                            <Formik
                                initialValues={{
                                    name: currentNameAddress,
                                    address: currentAddressAddress
                                }}
                                enableReinitialize
                                validationSchema={RegisterAddressSchema}
                                onSubmit={editOperation ? handleUpdateAddress : handleRegisterAddress}
                                innerRef={editAddressFormRef}
                            >
                                {({isSubmitting}) => (
                                    <Form>
                                        <div className="mb-3">
                                            <label htmlFor="name" className="form-label">{t("user_profile.name_label")}</label>
                                            <Field type="text" className="form-control" name="name" autoComplete="name" id="name"/>
                                            <ErrorMessage name="name" className="form-error" component="div"/>
                                        </div>
                                        <div className="mb-3">
                                            <label htmlFor="address"
                                                className="form-label">{t("user_profile.address_label")}</label>
                                            <Field type="text" className="form-control" name="address" autoComplete="address" id="address"/>
                                            {addressRepeated && <div className="form-error">{t("validation.my_profile.address_exits")}</div>}
                                            <ErrorMessage name="address" className="form-error" component="div"/>
                                        </div>
                                        <button className="btn btn-primary" type="submit" disabled={isSubmitting}>{t("user_profile.add_address")}</button>
                                    </Form>
                                )}
                            </Formik>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default UserProfile;
