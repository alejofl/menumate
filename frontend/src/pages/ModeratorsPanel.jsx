import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import "./styles/moderators_panel.styles.css";
import {useInfiniteQuery, useMutation, useQuery, useQueryClient} from "@tanstack/react-query";
import {useContext, useState} from "react";
import {useApi} from "../hooks/useApi.js";
import {useSearchParams} from "react-router-dom";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import {DEFAULT_RESTAURANT_COUNT, ROLES} from "../utils.js";
import RestaurantCardWithSnackbar from "../components/RestaurantCardWithSnackbar.jsx";
import ApiContext from "../contexts/ApiContext.jsx";
import ReportsModal from "../components/ReportsModal.jsx";
import {ErrorMessage, Field, Form, Formik} from "formik";
import {useUserService} from "../hooks/services/useUserService.js";
import AuthContext from "../contexts/AuthContext.jsx";
import {AddModeratorSchema} from "../data/validation.js";

function ModeratorsPanel() {
    const { t } = useTranslation();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const api = useApi();
    const userService = useUserService(api);
    const restaurantService = useRestaurantService(api);

    const [queryParams] = useSearchParams();
    const queryClient = useQueryClient();

    const {
        isPending: restaurantsIsPending,
        isError: restaurantsIsError,
        data: restaurants,
        error: restaurantsError,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["moderators", "restaurants"],
        queryFn: async ({ pageParam }) => (
            await restaurantService.getRestaurantsWithUnhandledReports(
                apiContext.restaurantsUrl,
                {
                    page: pageParam,
                    ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true
    });
    const {
        isPending: moderatorsIsPending,
        isError: moderatorsIsError,
        data: moderators,
        error: moderatorsError
    } = useQuery({
        queryKey: ["moderators", "users"],
        queryFn: async () => (
            await userService.getUsers(
                apiContext.usersUrl,
                {
                    role: ROLES.MODERATOR
                }
            )
        )
    });

    const addModeratorMutation = useMutation({
        mutationFn: async ({email}) => (
            await userService.addModerator(
                apiContext.usersUrl,
                email
            )
        )
    });
    const deleteModeratorMutation = useMutation({
        mutationFn: async (url) => (
            await userService.deleteModerator(url)
        ),
        onSuccess: () => queryClient.invalidateQueries({queryKey: ["moderators", "users"]}),
        onError: () => setShowEditModeratorErrorAlert(true)
    });

    const [showReportsModal, setShowReportsModal] = useState(false);
    const [restaurantUrl, setRestaurantUrl] = useState("");
    const [restaurantName, setRestaurantName] = useState("");
    const [restaurantId, setRestaurantId] = useState("");
    const [reportsUrl, setReportsUrl] = useState("");

    const [showEditModeratorErrorAlert, setShowEditModeratorErrorAlert] = useState(false);
    const [moderatorUrl, setModeratorUrl] = useState("");

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    const handleAddModerator = (values, {setSubmitting, resetForm}) => {
        addModeratorMutation.mutate(
            {
                email: values.email
            },
            {
                onSuccess: () => {
                    queryClient.invalidateQueries({queryKey: ["moderators", "users"]});
                    resetForm();
                },
                onError: () => setShowEditModeratorErrorAlert(true)
            }
        );
        setSubmitting(false);
    };

    if (restaurantsIsError) {
        return (
            <>
                <Error errorNumber={restaurantsError.response.status}/>
            </>
        );
    } else if (moderatorsIsError) {
        return (
            <>
                <Error errorNumber={moderatorsError.response.status}/>
            </>
        );
    }
    return (
        <>
            <Page title={t("titles.moderators_panel")} className="moderators_panel">
                <h1 className="page-title">{t("titles.moderators_panel")}</h1>
                <button type="button" className="btn btn-secondary mb-4" data-bs-toggle="modal" data-bs-target=".moderators_panel #edit-moderators-modal">{t("moderators_panel.edit.title")}</button>
                <div className="restaurant-feed">
                    {
                        restaurantsIsPending || moderatorsIsPending
                            ?
                            new Array(DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
                                return (
                                    <ContentLoader key={i} backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="18rem" height="18rem">
                                        <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
                                    </ContentLoader>
                                );
                            })
                            :
                            (restaurants.pages[0]?.content.length || 0) === 0
                                ?
                                <div className="empty-results">
                                    <h1><i className="bi bi-slash-circle default"></i></h1>
                                    <p>{t("restaurants.no_results")}</p>
                                </div>
                                :
                                restaurants.pages.flatMap(page => page.content).map((restaurant) => {
                                    return (
                                        <RestaurantCardWithSnackbar
                                            key={restaurant.restaurantId}
                                            restaurantId={restaurant.restaurantId}
                                            name={restaurant.name}
                                            mainImage={restaurant.portrait1Url}
                                            hoverImage={restaurant.portrait2Url}
                                            address={restaurant.address}
                                            snackbarText={t("moderators_panel.unhandled_reports", {count: restaurant.unhandledReportsCount})}
                                            isSnackbarSuccess={false}
                                            isSnackbarDanger={restaurant.unhandledReportsCount > 0}
                                            clickable={false}
                                            onClick={() => {
                                                setRestaurantUrl(restaurant.selfUrl);
                                                setRestaurantId(restaurant.restaurantId);
                                                setRestaurantName(restaurant.name);
                                                setReportsUrl(restaurant.reportsUrl);
                                                setShowReportsModal(true);
                                            }}
                                        />
                                    );
                                })
                    }
                    {
                        isFetchingNextPage &&
                        new Array(queryParams.get("size") || DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
                            return (
                                <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="18rem" height="18rem" key={i}>
                                    <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
                                </ContentLoader>
                            );
                        })
                    }
                </div>
                {
                    hasNextPage &&
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
                {
                    showReportsModal &&
                    <ReportsModal
                        restaurantUrl={restaurantUrl}
                        restaurantId={restaurantId}
                        restaurantName={restaurantName}
                        reportsUrl={reportsUrl}
                        onClose={() => setShowReportsModal(false)}
                    />
                }

                {
                    !moderatorsIsPending &&
                    <>
                        <div className="modal fade" id="edit-moderators-modal" tabIndex="-1">
                            <div className="modal-dialog modal-dialog-centered modal-lg modal-dialog-scrollable">
                                <div className="modal-content">
                                    <div className="modal-header">
                                        <h1 className="modal-title fs-5">{t("moderators_panel.edit.title")}</h1>
                                        <button type="button" className="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                                    </div>
                                    <div className="modal-body">
                                        <div>
                                            <h4>{t("moderators_panel.edit.add")}</h4>
                                            {showEditModeratorErrorAlert &&
                                                <div className="alert alert-danger" role="alert">{t("moderators_panel.edit.error")}</div>}
                                            <Formik
                                                initialValues={{
                                                    email: ""
                                                }}
                                                validationSchema={AddModeratorSchema}
                                                onSubmit={handleAddModerator}
                                            >
                                                {({isSubmitting}) => (
                                                    <Form>
                                                        <div className="mb-3">
                                                            <label htmlFor="email" className="form-label">{t("moderators_panel.edit.email")}</label>
                                                            <Field name="email" type="email" className="form-control" id="email"/>
                                                            <ErrorMessage name="email" className="form-error" component="div"/>
                                                            <div className="form-text">
                                                                {t("moderators_panel.edit.warning")}
                                                            </div>
                                                        </div>
                                                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>{t("moderators_panel.edit.add")}</button>
                                                    </Form>
                                                )}
                                            </Formik>
                                        </div>
                                        <div className="mt-4">
                                            <h4>{t("moderators_panel.edit.moderators")}</h4>
                                            <ul className="list-group list-group-flush">
                                                {
                                                    moderators.map((moderator, i) => (
                                                        <li className="list-group-item d-flex align-items-center" key={i}>
                                                            <i className="bi bi-person me-3"></i>
                                                            <div className="d-flex justify-content-between align-items-center w-100">
                                                                <p className="mb-0">{moderator.name} <a href={`mailto:${moderator.email}`}>&lt;{moderator.email}&gt;</a>
                                                                </p>
                                                                <div className="d-flex align-items-center">
                                                                    {
                                                                        moderator.selfUrl !== authContext.selfUrl &&
                                                                        <i
                                                                            className="bi bi-trash-fill text-danger default clickable-object"
                                                                            onClick={() => setModeratorUrl(moderator.selfUrl)}
                                                                            data-bs-toggle="modal"
                                                                            data-bs-target=".moderators_panel #delete-moderator-modal"
                                                                        ></i>
                                                                    }
                                                                </div>
                                                            </div>
                                                        </li>
                                                    ))
                                                }
                                            </ul>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div className="modal fade" id="delete-moderator-modal" tabIndex="-1">
                            <div className="modal-dialog modal-dialog-centered">
                                <div className="modal-content">
                                    <div className="modal-body">
                                        <h1 className="modal-title fs-5">{t("moderators_panel.edit.delete")}</h1>
                                    </div>
                                    <div className="modal-footer">
                                        <button
                                            type="button"
                                            data-bs-target=".moderators_panel #edit-moderators-modal"
                                            data-bs-toggle="modal" className="btn btn-secondary"
                                        >
                                            {t("paging.no")}
                                        </button>
                                        <button
                                            type="button"
                                            data-bs-target=".moderators_panel #edit-moderators-modal"
                                            data-bs-toggle="modal"
                                            className="btn btn-danger"
                                            onClick={() => deleteModeratorMutation.mutate(moderatorUrl)}
                                        >
                                            {t("paging.yes")}
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </>
                }
            </Page>
        </>
    );
}

export default ModeratorsPanel;
