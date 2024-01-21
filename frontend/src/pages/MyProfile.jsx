import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import "./styles/myprofile.styles.css";
import {useApi} from "../hooks/useApi.js";
import {useContext, useState} from "react";
import {useUserService} from "../hooks/services/useUserService.js";
import {useInfiniteQuery, useQueries, useQuery} from "@tanstack/react-query";
import AuthContext from "../contexts/AuthContext.jsx";
import {useSearchParams} from "react-router-dom";
import {useOrderService} from "../hooks/services/useOrderService.js";
// import {useRestaurantService} from "../hooks/services/useRestaurantService.js";


function MyProfile() {
    const STAR_RATING = 5;
    const { t } = useTranslation();
    const api = useApi();
    const authContext = useContext(AuthContext);
    const userService = useUserService(api);
    const orderService = useOrderService(api);
    // const restaurantService = useRestaurantService(api);

    const [queryParams] = useSearchParams();
    const [query] = useState({
        ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
    });

    const { data : User } = useQuery({
        queryKey: ["user", "myProfile"],
        queryFn: async () => (
            await userService.getUser(
                authContext.selfUrl
            )
        )
    });

    const {
        isPending,
        data: addresses
    } = useQuery({
        queryKey: ["addresses"],
        queryFn: async () => (
            await userService.getAddresses(
                User.addressesUrl
            )
        ),
        enabled: !!User
    });

    const {
        isPending: reviewsPending,
        data: reviews
    } = useInfiniteQuery( {
        queryKey: ["reviews"],
        queryFn: async ({ pageParam }) => (
            await userService.getReviews(
                User.reviewsUrl,
                {
                    ...query,
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true,
        enabled: !!User
    });

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
            []
    });
    console.log(orders);

    /*
     * const restaurants = useQueries({
     *     queries: orders
     *         ?
     *         orders.map(order => {
     *             return {
     *                 queryKey: ["restaurant", order.restaurantId, order.restaurantsUrl],
     *                 queryFn: async () => (
     *                     await restaurantService.getRestaurant(order.restaurantsUrl, true)
     *                 )
     *             };
     *         })
     *         :
     *         [],
     *     enabled: !isPending
     * });
     *
     * console.log(restaurants);
     */

    return (
        <>
            <Page title={t("titles.myprofile")} className="myprofile">
                <div className="profile-container">
                    <div className="user-info-container">
                        <div className="card">
                            <div className="card-body">
                                <h3 className="card-title">{t("myprofile.profile")}</h3>
                                <label htmlFor="exampleFormControlInput1" className="form-label">{t("myprofile.name_label")}</label>
                                <input type="email" className="form-control" id="exampleFormControlInput1" disabled value={User?.name}/>
                                <label htmlFor="exampleFormControlInput1" className="form-label">{t("myprofile.email_label")}</label>
                                <input type="email" className="form-control" id="exampleFormControlInput1" value={User?.email} disabled/>
                                <hr/>
                                <h3 className="card-title">{t("myprofile.addresses")}</h3>
                                {!isPending && addresses.map(address => (
                                    <li key={address} className="list-group-item d-flex align-items-center justify-content-between px-0 address-list">
                                        <div className="d-flex align-items-center ">
                                            <i className="bi bi-geo-alt"></i>
                                        </div>
                                        <div className="d-flex gap-3">{address.address}</div>
                                    </li>
                                ))}
                            </div>
                        </div>
                    </div>
                    <div className="reviews-container">
                        <h3 className="page-title">{t("myprofile.reviews")}</h3>
                        {!reviewsPending && reviews.pages.flatMap(page => page.content).map(review => (
                            <div className="card" key={review.orderId}>
                                <div className="card-header">
                                    <div className="my-review-card-header">
                                        <div className="my-review-card-header-info">
                                            {/* {restaurants.map(restaurant => (<h1 key={restaurant.restaurantId}>{restaurant.name}</h1>))}*/}
                                            {/* <b><c:out value="${review.order.restaurant.name}"/></b>*/}
                                            <small className="text-muted">{review.date.toLocaleString()}</small>
                                        </div>
                                        <div className="d-flex gap-2 align-items-baseline mb-2 ">
                                            <div className="small-ratings">
                                                {[...Array(review.rating)].map(i => (
                                                    <i key={i} className="bi bi-star-fill rating-color"></i>
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
                </div>
            </Page>
        </>
    );
}

export default MyProfile;
