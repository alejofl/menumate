import Page from "../components/Page.jsx";
import {useTranslation} from "react-i18next";
import "./styles/user_restaurants.styles.css";
import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import {useContext} from "react";
import AuthContext from "../contexts/AuthContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";
import {useApi} from "../hooks/useApi.js";
import {useSearchParams} from "react-router-dom";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import {DEFAULT_RESTAURANT_COUNT} from "../utils.js";
import RestaurantCardWithSnackbar from "../components/RestaurantCardWithSnackbar.jsx";

export function UserRestaurants() {
    const { t } = useTranslation();
    const authContext = useContext(AuthContext);
    const api = useApi();
    const userService = useUserService(api);
    const restaurantService = useRestaurantService(api);

    const [queryParams] = useSearchParams();

    const { isError: userIsError, data: userData, error: userError} = useQuery({
        queryKey: ["user", authContext.selfUrl],
        queryFn: async () => (
            await userService.getUser(authContext.selfUrl)
        ),
        enabled: authContext.isAuthenticated
    });

    const {
        isPending,
        isError: restaurantsIsError,
        data: restaurants,
        error: restaurantsError,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["user", authContext.selfUrl, "restaurants"],
        queryFn: async ({ pageParam }) => (
            await restaurantService.getRestaurants(
                userData.restaurantsEmployedAtUrl,
                {
                    page: pageParam,
                    ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: !!userData
    });

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    if (userIsError) {
        return (
            <>
                <Error errorNumber={userError.response.status}/>
            </>
        );
    } else if (restaurantsIsError) {
        return (
            <>
                <Error errorNumber={restaurantsError.response.status}/>
            </>
        );
    }
    return (
        <>
            <Page title={t("titles.user_restaurants")} className="user_restaurants">
                <h1 className="page-title">{t("titles.user_restaurants")}</h1>
                <div className="restaurant-feed">
                    {
                        isPending
                            ?
                            new Array(DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
                                return (
                                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="18rem" height="18rem" key={i}>
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
                                restaurants.pages.flatMap(page => page.content).map(restaurant => {
                                    return (
                                        <RestaurantCardWithSnackbar
                                            key={restaurant.selfUrl}
                                            restaurantId={restaurant.restaurantId}
                                            name={restaurant.name}
                                            mainImage={restaurant.portrait1Url}
                                            hoverImage={restaurant.portrait2Url}
                                            address={restaurant.address}
                                            snackbarText={t("user_restaurants.pending_orders", {count: restaurant.pendingOrderCount})}
                                            isSnackbarSuccess={restaurant.pendingOrderCount === 0}
                                            isSnackbarDanger={restaurant.pendingOrderCount > 0}
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
            </Page>
        </>
    );
}
