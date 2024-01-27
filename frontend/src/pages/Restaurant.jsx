import React, {useState} from "react";
import {Link, useParams, useSearchParams} from "react-router-dom";
import Page from "../components/Page.jsx";
import {useQueries, useQuery} from "@tanstack/react-query";
import {useTranslation} from "react-i18next";
import {useApi} from "../hooks/useApi.js";
import {useContext} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import {useRestaurantService} from "../hooks/services/useRestaurantService.js";
import Error from "./Error.jsx";
import ContentLoader from "react-content-loader";
import "./styles/restaurant.styles.css";
import Rating from "../components/Rating.jsx";
import TagsContainer from "../components/TagsContainer.jsx";
import ProductCard from "../components/ProductCard.jsx";
import {MAXIMUM_CART_ITEMS, PRICE_DECIMAL_DIGITS} from "../utils.js";
import ReviewsModal from "../components/ReviewsModal.jsx";
import RestaurantLocationToast from "../components/RestaurantLocationToast.jsx";
import PlaceOrderModal from "../components/PlaceOrderModal.jsx";
import OrderPlacedAnimation from "../components/OrderPlacedAnimation.jsx";
import RestaurantReportToast from "../components/RestaurantReportToast.jsx";
import AuthContext from "../contexts/AuthContext.jsx";
import {useUserService} from "../hooks/services/useUserService.js";
import EditRestaurant from "./EditRestaurant.jsx";

function Restaurant({edit = false}) {
    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const authContext = useContext(AuthContext);
    const restaurantService = useRestaurantService(api);
    const userService = useUserService(api);

    const { restaurantId } = useParams();
    const { isError: restaurantIsError, data: restaurant, error: restaurantError, refetch: refetchRestaurant } = useQuery({
        queryKey: ["restaurant", restaurantId],
        queryFn: async () => (
            await restaurantService.getRestaurant(apiContext.restaurantsUriTemplate.fill({restaurantId: restaurantId}), true)
        )
    });
    const { isPending: categoriesIsPending, isError: categoriesIsError, data: categories, error: categoriesError, refetch: refetchCategories } = useQuery({
        queryKey: ["restaurant", restaurantId, "categories"],
        queryFn: async () => (
            await restaurantService.getCategories(restaurant.categoriesUrl)
        ),
        enabled: !!restaurant
    });
    const products = useQueries({
        queries: categories
            ?
            categories.sort((a, b) => a.orderNum < b.orderNum).map(category => {
                return {
                    queryKey: ["restaurant", restaurantId, "category", category.orderNum, "products"],
                    queryFn: async () => (
                        await restaurantService.getProducts(category.productsUrl)
                    )
                };
            })
            :
            []
    });
    const { isPending: promotionsIsPending, isError: promotionsIsError, data: promotions, error: promotionsError, refetch: refetchPromotions } = useQuery({
        queryKey: ["restaurant", restaurantId, "promotions"],
        queryFn: async () => (
            await restaurantService.getPromotions(restaurant.promotionsUrl)
        ),
        enabled: !!restaurant
    });
    const promotionProducts = useQueries({
        queries: promotions
            ?
            promotions.map(promotion => {
                return {
                    queryKey: ["restaurant", restaurantId, "promotion", promotion.destinationUrl],
                    queryFn: async () => (
                        await restaurantService.getProduct(promotion.destinationUrl)
                    )
                };
            })
            :
            []
    });
    const { isError: userIsError, data: user, error: userError} = useQuery({
        queryKey: ["user", authContext.selfUrl],
        queryFn: async () => (
            await userService.getUser(authContext.selfUrl)
        ),
        enabled: authContext.isAuthenticated
    });
    const { isPending: userRoleIsPending, isError: userRoleIsError, data: userRole, error: userRoleError} = useQuery({
        queryKey: ["restaurant", restaurantId, "employees", user?.userId],
        queryFn: async () => (
            await userService.getRoleForRestaurant(
                restaurant.employeesUriTemplate.fill({userId: user.userId})
            )
        ),
        enabled: !userIsError && !restaurantIsError && !!user && !!restaurant
    });

    const [cart, setCart] = useState([]);
    const addProductToCart = (productId, name, price, quantity, comments) => {
        setCart([...cart, {productId: productId, name: name, price: price, quantity: quantity, comments: comments}]);
    };

    const [showReviewModal, setShowReviewModal] = useState(false);
    const [showPlaceOrderModal, setShowPlaceOrderModal] = useState(false);
    const [showOrderPlacedAnimation, setShowOrderPlacedAnimation] = useState(false);

    const [queryParams] = useSearchParams();

    if (showOrderPlacedAnimation) {
        return (
            <OrderPlacedAnimation/>
        );
    } else if (restaurantIsError) {
        return (
            <>
                <Error errorNumber={restaurantError.response.status}/>
            </>
        );
    } else if (categoriesIsError) {
        return (
            <>
                <Error errorNumber={categoriesError.response.status}/>
            </>
        );
    } else if (products.some(product => product.isError) || promotionProducts.some(product => product.isError)) {
        return (
            <>
                <Error errorNumber="500"/>
            </>
        );
    } else if (promotionsIsError) {
        return (
            <>
                <Error errorNumber={promotionsError.response.status}/>
            </>
        );
    } else if (userIsError) {
        return (
            <>
                <Error errorNumber={userError.response.status}/>
            </>
        );
    } else if (!categoriesIsPending && !restaurant.active && userRoleIsError) {
        return (
            <>
                <Error errorNumber="404"/>
            </>
        );
    } else if (userRoleIsError) {
        return (
            <>
                <Error errorNumber={userRoleError.response.status}/>
            </>
        );
    } else if (categoriesIsPending || promotionsIsPending || products.some(product => product.isPending) || promotionProducts.some(product => product.isPending) || (authContext.isAuthenticated && userRoleIsPending)) {
        return (
            <>
                <Page title={t("titles.loading")}>
                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="1920">
                        <rect x="288" y="50" rx="0" ry="0" width="1344" height="200"/>
                        <rect x="12" y="300" rx="0" ry="0" width="200" height="380"/>
                        <rect x="236" y="300" rx="0" ry="0" width="1268" height="64"/>
                        <rect x="236" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="656" y="388" rx="0" ry="0" width="400" height="200"/>
                        <rect x="1528" y="300" rx="0" ry="0" width="380" height="120"/>
                    </ContentLoader>
                </Page>
            </>
        );
    } else if (restaurant.deleted) {
        return (
            <>
                <Error errorNumber="410"/>
            </>
        );
    } else if (edit && authContext.isAuthenticated && userRole.isAdmin) {
        return (
            <>
                <EditRestaurant
                    restaurant={restaurant}
                    refetchRestaurant={refetchRestaurant}
                    categories={categories}
                    refetchCategories={refetchCategories}
                    products={products}
                    promotions={promotions}
                    refetchPromotions={refetchPromotions}
                    promotionProducts={promotionProducts}
                    userRole={userRole}
                />
            </>
        );
    }
    return (
        <>
            <Page title={restaurant.name} className="restaurant">
                <div className="header">
                    <img src={restaurant.portrait1Url} alt={restaurant.name}/>
                </div>
                <div className="d-flex justify-content-center">
                    <div className="information">
                        <img src={restaurant.logoUrl} alt={restaurant.name} className="logo"/>
                        <div className="flex-grow-1">
                            {
                                !restaurant.active &&
                                <div className="alert alert-warning" role="alert">
                                    <b>{t("restaurant.disabled")}</b> {userRole.isOrderHandler ? t("restaurant.disabled_employee") : t("restaurant.disabled_user")}
                                </div>
                            }
                            <h1>{restaurant.name}</h1>
                            <p className="mb-1">
                                {restaurant.description || <i>{t("restaurant.no_description")}</i>}
                            </p>
                            <p className="mb-2"><i className="bi bi-geo-alt"></i> {restaurant.address}</p>
                            {
                                restaurant.reviewCount === 0
                                    ?
                                    <div className="mb-2">
                                        <small className="text-muted">{t("restaurant.no_reviews")}</small>
                                    </div>
                                    :
                                    <>
                                        <Rating rating={restaurant.averageRating} count={restaurant.reviewCount}/>
                                        <button className="btn btn-link" type="button" onClick={() => setShowReviewModal(true)}>
                                            <small>{t("restaurant.view_reviews")}</small>
                                        </button>
                                    </>
                            }
                            <TagsContainer tags={restaurant.tags} clickable={true}/>
                        </div>
                        <div className="d-flex flex-column gap-2">
                            {authContext.isAuthenticated && userRole.isAdmin &&
                                <Link to="edit" className="btn btn-secondary" type="button">{t("restaurant.edit_menu")}</Link>
                            }
                            {authContext.isAuthenticated && userRole.isOrderHandler &&
                                <Link to="orders" className="btn btn-secondary" type="button">{t("restaurant.see_orders")}</Link>
                            }
                        </div>
                    </div>
                </div>
                <div className="content">
                    <div className="categories sticky">
                        <div className="card">
                            <div className="card-header text-muted">{t("restaurant.categories")}</div>
                            <div className="card-body">
                                <div className="nav nav-pills flex-column small">
                                    {
                                        categories.sort((a, b) => a.orderNum < b.orderNum).map(category => (
                                            <button
                                                className="category-item nav-link"
                                                key={category.orderNum}
                                                onClick={(event) => {
                                                    document.querySelector(`#category-${category.orderNum}`).scrollIntoView({block: "start"});
                                                    document.querySelector(".category-item.active")?.classList.remove("active");
                                                    event.currentTarget.classList.add("active");
                                                }}
                                            >
                                                {category.name}
                                            </button>
                                        ))
                                    }
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="menu px-4">
                        {promotions.length > 0 &&
                            <React.Fragment>
                                <div className="card mb-4 bg-promotion">
                                    <div className="card-body d-flex justify-content-between align-items-center">
                                        <h3 className="mb-0 text-white">{t("restaurant.promotions")}</h3>
                                    </div>
                                </div>
                                <div className="product-container">
                                    {
                                        promotions.map((promotion, i) => (
                                            <ProductCard
                                                key={i}
                                                productId={promotionProducts[i].data.productId}
                                                name={promotionProducts[i].data.name}
                                                description={promotionProducts[i].data.description}
                                                price={promotionProducts[i].data.price}
                                                imageUrl={promotionProducts[i].data.imageUrl}
                                                discount={promotion.discountPercentage}
                                                addProductToCart={addProductToCart}
                                                disabled={cart.length > MAXIMUM_CART_ITEMS}
                                            />
                                        ))
                                    }
                                </div>
                            </React.Fragment>
                        }
                        {
                            categories.sort((a, b) => a.orderNum < b.orderNum).map((category, i) => (
                                <React.Fragment key={category.orderNum}>
                                    <div className="card mb-4" id={`category-${category.orderNum}`}>
                                        <div className="card-body d-flex justify-content-between align-items-center">
                                            <h3 className="mb-0">{category.name}</h3>
                                        </div>
                                    </div>
                                    <div className="product-container">
                                        {
                                            products[i].data.map((product, i) => (
                                                <ProductCard
                                                    key={i}
                                                    productId={product.productId}
                                                    name={product.name}
                                                    description={product.description}
                                                    price={product.price}
                                                    imageUrl={product.imageUrl}
                                                    addProductToCart={addProductToCart}
                                                    disabled={cart.length > MAXIMUM_CART_ITEMS}
                                                />
                                            ))
                                        }
                                    </div>
                                </React.Fragment>
                            ))
                        }
                    </div>
                    <div className="cart sticky">
                        <div className="card">
                            <div className="card-header text-muted">{t("restaurant.my_order")}</div>
                            <ul className="list-group list-group-flush">
                                {
                                    cart.map((product, i) => (
                                        <li className="list-group-item d-flex justify-content-between" key={i}>
                                            <div className="d-flex align-items-center gap-1">
                                                <span className="badge text-bg-secondary">x{product.quantity}</span>
                                                <span>{product.name}</span>
                                            </div>
                                            <span className="no-wrap"><strong>${(product.price * product.quantity).toFixed(PRICE_DECIMAL_DIGITS)}</strong></span>
                                        </li>
                                    ))
                                }
                            </ul>
                            <div className="card-body d-flex">
                                <button className={`btn btn-primary flex-grow-1 ${!restaurant.active ? "disabled" : ""}`} id="place-order-button" type="button" disabled={cart.length === 0 || !restaurant.active} onClick={() => setShowPlaceOrderModal(true)}>
                                    {t("restaurant.place_order")}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <RestaurantLocationToast restaurantId={restaurantId} dineIn={queryParams.has("qr")}/>
                <RestaurantReportToast reportsUrl={restaurant.reportsUrl}/>
            </Page>
            {showReviewModal &&
                <ReviewsModal
                    reviewsUrl={restaurant.reviewsUrl}
                    isEmployee={authContext.isAuthenticated && userRole.isOrderHandler}
                    onClose={() => setShowReviewModal(false)}
                />
            }
            {restaurant.active && showPlaceOrderModal &&
                <PlaceOrderModal
                    restaurantId={restaurantId}
                    maxTables={restaurant.maxTables}
                    dineIn={queryParams.has("qr")}
                    dineInCompletionTime={restaurant.dineInCompletionTime}
                    takeAwayCompletionTime={restaurant.takeAwayCompletionTime}
                    deliveryCompletionTime={restaurant.deliveryCompletionTime}
                    cart={cart}
                    onOrderCompleted={() => setShowOrderPlacedAnimation(true)}
                    onClose={() => setShowPlaceOrderModal(false)}
                />
            }
        </>
    );
}

export default Restaurant;
