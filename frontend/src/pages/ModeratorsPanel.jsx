import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import "./styles/moderators_panel.styles.css";
import {useInfiniteQuery} from "@tanstack/react-query";
import {useContext, useState} from "react";
import {useApi} from "../hooks/useApi.js";
import {useSearchParams} from "react-router-dom";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import {DEFAULT_RESTAURANT_COUNT} from "../utils.js";
import RestaurantCardWithSnackbar from "../components/RestaurantCardWithSnackbar.jsx";
import ApiContext from "../contexts/ApiContext.jsx";
import ReportsModal from "../components/ReportsModal.jsx";

function ModeratorsPanel() {
    const { t } = useTranslation();
    const apiContext = useContext(ApiContext);
    const api = useApi();
    const restaurantService = useRestaurantService(api);

    const [queryParams] = useSearchParams();

    const {
        isPending,
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

    const [showReportsModal, setShowReportsModal] = useState(false);
    const [restaurantUrl, setRestaurantUrl] = useState("");
    const [restaurantName, setRestaurantName] = useState("");
    const [restaurantId, setRestaurantId] = useState("");
    const [reportsUrl, setReportsUrl] = useState("");

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    if (restaurantsIsError) {
        return (
            <>
                <Error errorNumber={restaurantsError.response.status}/>
            </>
        );
    }
    return (
        <>
            <Page title={t("titles.moderators_panel")} className="moderators_panel">
                <h1 className="page-title">{t("titles.moderators_panel")}</h1>
                <div className="restaurant-feed">
                    {
                        isPending
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
            </Page>
        </>
    );
}

export default ModeratorsPanel;
