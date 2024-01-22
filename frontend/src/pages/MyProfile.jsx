import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import "./styles/myprofile.styles.css";
import {useApi} from "../hooks/useApi.js";
import {useContext, useState} from "react";
import {useUserService} from "../hooks/services/useUserService.js";
import {useInfiniteQuery, useMutation, useQueries, useQuery} from "@tanstack/react-query";
import AuthContext from "../contexts/AuthContext.jsx";
import {useSearchParams} from "react-router-dom";
import {useOrderService} from "../hooks/services/useOrderService.js";
import ContentLoader from "react-content-loader";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import ErrorModal from "../components/ErrorModal.jsx";
import Error from "./Error.jsx";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {RegisterAddressSchema} from "../data/validation.js";
import {BAD_REQUEST_STATUS_CODE, EMAIL_ALREADY_IN_USE_ERROR} from "../utils.js";



function MyProfile() {
    const STAR_RATING = 5;
    const DEFAULT_ORDER_COUNT = 12;
    const { t } = useTranslation();
    const api = useApi();
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);
    const orderService = useOrderService(api);
    const restaurantService = useRestaurantService(api);

    const [queryParams] = useSearchParams();
    const [query] = useState({
        ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
    });
    const queryKey = useState(query);
    const [currentModal, setCurrentModal] = useState();

    const {
        isPending : userIsPending,
        isError : userIsError,
        error : userError,
        data : user
    } = useQuery({
        queryKey: ["user", "myProfile"],
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
        queryKey: ["addresses"],
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
        queryKey: ["reviews", queryKey],
        queryFn: async ({ pageParam }) => (
            await userService.getReviews(
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
    console.log(reviews);

    const orders = useQueries({
        queries: reviews
            ?
            reviews.pages.flatMap(page => page.content).map(review => {
                return {
                    queryKey: ["order", review.orderId, review.orderUrl],
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
                    queryKey: ["restaurant", order.data.orderId],
                    queryFn: async () => {
                        console.log("PARECE QUE TENGO EL LINK");
                        console.log(order.data.restaurantUrl);
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
                onSuccess: () => handleCloseRegisterAddressModal(),
                onError: (error) => {
                    if (error.response.status === BAD_REQUEST_STATUS_CODE) {
                        EMAIL_ALREADY_IN_USE_ERROR.message;
                    }
                }
            }
        );
        setSubmitting(false);
    };

    const handleOpenRegisterAddressModal = () => {
        // eslint-disable-next-line no-undef
        const modal = new bootstrap.Modal(document.querySelector(".registerAddressModal .modal"));
        modal.show();
    };

    const handleCloseRegisterAddressModal = () => {
        window.location.reload();
    };

    const deleteAddressMutation = useMutation({
        mutationFn: async ({url}) => {
            await userService.deleteAddress(url);
        }
    });

    const handleDeleteAddress = () => {
        deleteAddressMutation.mutate(
            {
                url: currentModal
            },
            {
                onSuccess: () => handleCloseAndReloadDeleteAddressModal(),
                onError: (error) => {
                    if (error.response.status === BAD_REQUEST_STATUS_CODE) {
                        EMAIL_ALREADY_IN_USE_ERROR.message;
                    }
                }
            }
        );
    };

    const handleOpenDeleteAddressModal = (url) => {
        console.log("ESTE ES EL URL DE LA ADDRESS QUE QUIERO DELETEAR");
        console.log(url);
        setCurrentModal(url);
        // eslint-disable-next-line no-undef
        const modal = new bootstrap.Modal(document.querySelector(".registerDeleteModal .modal"));
        modal.show();
    };

    const handleCloseDeleteAddressModal = () => {
        const modalElement = document.querySelector(".registerDeleteModal .modal");

        if (modalElement) {
            // eslint-disable-next-line no-undef
            const modal = bootstrap.Modal.getInstance(modalElement);
            if (modal) {
                modal.hide();
            }
        }
    };

    const handleCloseAndReloadDeleteAddressModal = () => {
        window.location.reload();
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
    console.log(addresses);

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    return (
        <>
            <Page title={t("titles.myprofile")} className="myprofile">
                <div className="profile-container">
                    <div className="user-info-container">
                        <div className="card">
                            <div className="card-body">
                                <h3 className="card-title">{t("myprofile.profile")}</h3>
                                <label htmlFor="exampleFormControlInput1"
                                    className="form-label">{t("myprofile.name_label")}</label>
                                <input type="email" className="form-control" id="exampleFormControlInput1" disabled
                                    value={user?.name}/>
                                <label htmlFor="exampleFormControlInput1"
                                    className="form-label">{t("myprofile.email_label")}</label>
                                <input type="email" className="form-control" id="exampleFormControlInput1"
                                    value={user?.email} disabled/>
                                <hr/>
                                <h3 className="card-title">{t("myprofile.addresses")}</h3>
                                <ul className="px-0">
                                    {addresses?.map(address => (
                                        <li key={address}
                                            className="list-group-item d-flex align-items-center justify-content-between px-0 address-list">
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
                                            <div className="d-flex gap-3">
                                                {
                                                    !address.name
                                                    &&
                                                    <a className="add-address-modal-button" type="button"><i
                                                        className="bi bi-save-fill text-success right-button"></i></a>
                                                }
                                                <a className="delete-address-modal-button" type="button"
                                                    onClick={() => handleOpenDeleteAddressModal(address.selfUrl)}><i
                                                        className="bi bi-trash-fill text-danger right-button"></i></a>
                                            </div>
                                        </li>
                                    ))}
                                </ul>
                                <div className="d-flex mt-2">
                                    <button className="btn btn-primary flex-grow-1" type="button"
                                        onClick={handleOpenRegisterAddressModal}>
                                        {t("myprofile.add_address")}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div className="reviews-container">
                        <h3 className="page-title">{t("myprofile.reviews")}</h3>
                        <div>
                            {
                                reviewsIsLoading || orders.some(order => order.isPending) || restaurants.some(restaurant => restaurant.isPending)
                                    ?
                                    new Array(DEFAULT_ORDER_COUNT).fill("").map((_, i) => {
                                        return (
                                            <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0"
                                                width="100%" height="110" key={i}>
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
                                        <div>
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
                                                                <small
                                                                    className="text-muted">{review.date.toLocaleString()}</small>
                                                            </div>
                                                            <div className="d-flex gap-2 align-items-baseline mb-2 ">
                                                                <div className="small-ratings">
                                                                    {[...Array(review.rating)].map(i => (
                                                                        <i key={i}
                                                                            className="bi bi-star-fill rating-color"></i>
                                                                    ))}
                                                                    {[...Array(STAR_RATING - review.rating)].map(i => (
                                                                        <i key={i} className="bi bi-star-fill"></i>
                                                                    ))}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </div>
                                                    <div className="card-body">
                                                        <p>{review.comment}</p>
                                                    </div>
                                                </div>
                                            ))}
                                        </div>
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
                        </div>
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

            <div className="registerDeleteModal">
                <div className="modal fade" id="delete-address-modal" tabIndex="-1">
                    <div className="modal-dialog modal-dialog-centered">
                        <div className="modal-content">
                            <div className="modal-body">
                                <h1 className="modal-title fs-5">
                                    {t("myprofile.delete_address_title")}
                                </h1>
                            </div>
                            <div className="modal-footer">
                                <button type="button" className="btn btn-secondary"
                                    onClick={handleCloseDeleteAddressModal}>
                                    {t("general_options.no")}
                                </button>
                                <button className="btn btn-danger" type="submit"
                                    onClick={handleDeleteAddress}>{t("general_options.yes")}</button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="registerAddressModal">
                <div className="modal" tabIndex="-1" id="newAddressModal">
                    <div className="modal-dialog">
                        <div className="modal-content">
                            <div className="modal-header"><h1 className="modal-title fs-5">
                                {t("myprofile.add_address")}
                            </h1>
                            </div>
                            <div className="modal-body">
                                <Formik
                                    initialValues={{
                                        name: "",
                                        address: ""
                                    }}
                                    validationSchema={RegisterAddressSchema}
                                    onSubmit={handleRegisterAddress}
                                >
                                    {({isSubmitting}) => (
                                        <Form>
                                            <div className="mb-3">
                                                <label htmlFor="name"
                                                    className="form-label">{t("myprofile.name_label")}</label>
                                                <Field type="text" className="form-control" name="name"
                                                    autoComplete="name" id="name"/>
                                                <ErrorMessage name="name" className="form-error" component="div"/>
                                            </div>
                                            <div className="mb-3">
                                                <label htmlFor="address"
                                                    className="form-label">{t("myprofile.address_label")}</label>
                                                <Field type="text" className="form-control" name="address"
                                                    autoComplete="address" id="address"/>
                                                <ErrorMessage name="address" className="form-error" component="div"/>
                                            </div>
                                            <button className="btn btn-primary" type="submit"
                                                disabled={isSubmitting}>{t("myprofile.add_address")}</button>
                                        </Form>
                                    )}
                                </Formik>
                            </div>
                            <div className="modal-footer">
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </>
    );
}

export default MyProfile;
