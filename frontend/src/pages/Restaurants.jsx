import { useTranslation } from "react-i18next";
import Page from "../components/Page.jsx";
import RestaurantCard from "../components/RestaurantCard.jsx";
import { useApi } from "../hooks/useApi.js";
import {useContext, useState} from "react";
import ApiContext from "../contexts/ApiContext.jsx";
import { useRestaurantService } from "../hooks/services/useRestaurantService.js";
import { useInfiniteQuery } from "@tanstack/react-query";
import ContentLoader from "react-content-loader";
import "./styles/restaurants.styles.css";
import Select from "react-select";
import {useSearchParams} from "react-router-dom";
import ErrorModal from "../components/ErrorModal.jsx";

function Restaurants() {
    const DEFAULT_RESTAURANT_COUNT = 12;
    const { t } = useTranslation();
    const api = useApi();
    const apiContext = useContext(ApiContext);
    const restaurantService = useRestaurantService(api);

    const [queryParams, setQueryParams] = useSearchParams();
    const [query, setQuery] = useState({
        search: queryParams.get("search") || "",
        specialties: queryParams.getAll("specialties") || [],
        tags: queryParams.getAll("tags") || [],
        orderBy: queryParams.get("orderBy") || "alphabetic",
        descending: queryParams.get("descending") === "true" || false,
        ...(queryParams.get("size") ? {size: queryParams.get("size")} : {})
    });
    const [queryKey, setQueryKey] = useState(query);

    const selectStyles = (last) => ({
        container: (defaultStyle) => ({
            ...defaultStyle,
            flexGrow: 1
        }),
        control: (defaultStyle, state) => ({
            ...defaultStyle,
            width: "100%",
            height: "100%",
            flexGrow: 1,
            minWidth: "0",
            padding: "0.375rem 0.75rem",
            marginLeft: "calc(var(--bs-border-width) * -1)",
            borderTopLeftRadius: "0",
            borderBottomLeftRadius: "0",
            fontSize: "1rem",
            fontWeight: 400,
            lineHeight: 1.5,
            color: "var(--bs-body-color)",
            backgroundColor: "var(--bs-body-bg)",
            backgroundClip: "padding-box",
            "-webkit-appearance": "none",
            "-moz-appearance": "none",
            appearance: "none",
            borderRadius: last ? "0" : "0.375rem",
            transition: "border-color .15s ease-in-out,box-shadow .15s ease-in-out",
            ":hover": {
                borderColor: state.isFocused ? "var(--primary)" : "var(--bs-border-color)"
            },
            boxShadow: state.isFocused ? "0 0 0 0.25rem #ed7a5740" : "none",
            border: state.isFocused ? "var(--bs-border-width) solid var(--primary)" : "var(--bs-border-width) solid var(--bs-border-color)"
        }),
        valueContainer: (defaultStyle) => ({
            ...defaultStyle,
            padding: 0
        }),
        placeholder: (defaultStyle) => ({
            ...defaultStyle,
            fontSize: "1rem",
            fontWeight: 400,
            lineHeight: 1.5,
            color: "var(--bs-secondary-color)"
        }),
        dropdownIndicator: (defaultStyle) => ({
            ...defaultStyle,
            color: "var(--bs-secondary-color)",
            paddingRight: 0
        }),
        option: (defaultStyle, state) => ({
            ...defaultStyle,
            backgroundColor: state.isSelected ? "var(--primary)" : state.isFocused ? "#ed7a5740" : "var(--bs-body-bg)",
            ":active": {
                backgroundColor: "var(--primary)",
                color: "white"
            }
        }),
        selectContainer: (defaultStyle) => ({
            ...defaultStyle,
            ":focus": {
                borderColor: "var(--primary)",
                outline: 0,
                boxShadow: "0 0 0 0.25rem #ed7a5740"
            }
        })
    });
    const selectComponents = {
        IndicatorSeparator: () => null,
        ClearIndicator: () => null
    };

    const specialties = [
        "american",
        "argentine",
        "asian",
        "barbecue",
        "breakfast",
        "cafe",
        "chinese",
        "desserts",
        "european",
        "fast_food",
        "french",
        "gluten_free",
        "grill",
        "greek",
        "hamburgers",
        "ice_cream",
        "indian",
        "italian",
        "japanese",
        "korean",
        "latin",
        "mediterranean",
        "mexican",
        "middle_eastern",
        "pizza",
        "seafood",
        "sushi",
        "steakhouse",
        "thai",
        "turkish",
        "vegan",
        "vegetarian"
    ].map(specialty => ({label: t(`restaurant_specialties.${specialty}`), value: specialty}));
    const tags = [
        "elegant",
        "casual",
        "cheap",
        "cosy",
        "family_friendly",
        "kid_friendly",
        "lgbt_friendly",
        "pet_friendly",
        "healthy",
        "sustainable",
        "locally_sourced",
        "historic",
        "trendy",
        "romantic",
        "outdoor_dining",
        "fine_dining",
        "international",
        "wheelchair_accessible",
        "live_music",
        "karaoke",
        "happy_hour",
        "old_fashioned"
    ].map(tag => ({label: t(`restaurant_tags.${tag}`), value: tag}));
    const orderBy = ["alphabetic", "rating", "price", "date"].map(order => ({label: t(`restaurants.options_order_by.${order}`), value: order}));

    const {
        isLoading,
        isError,
        data,
        isFetchingNextPage,
        hasNextPage,
        fetchNextPage
    } = useInfiniteQuery({
        queryKey: ["restaurants", "search", queryKey],
        queryFn: async ({ pageParam }) => (
            await restaurantService.getRestaurants(
                apiContext.restaurantsUrl,
                {
                    ...query,
                    page: pageParam
                }
            )
        ),
        getNextPageParam: (lastPage) => lastPage.nextPage?.page || undefined,
        keepPreviousData: true
    });

    const handleSearch = (event) => {
        event.preventDefault();
        setQueryParams({...query});
        setQueryKey(query);
    };

    const handleClearFilters = () => {
        setQuery({
            search: "",
            specialties: [],
            tags: [],
            orderBy: "alphabetic",
            descending: false
        });
        setQueryParams({...query});
        setQueryKey(query);
    };

    const handleLoadMoreContent = async () => {
        await fetchNextPage();
    };

    return (
        <>
            <Page title={t("titles.restaurants")} className="restaurants">
                <form onSubmit={handleSearch}>
                    <div className="search-form-container">
                        <div className="pb-2">
                            <div className="input-group flex-nowrap">
                                <span className="input-group-text search-input"><i className="bi bi-search"></i></span>
                                <input
                                    type="text"
                                    className="form-control search-input"
                                    placeholder={t("restaurants.search_placeholder")}
                                    value={query.search}
                                    onChange={(event) => setQuery({...query, search: event.target.value})}
                                />
                            </div>
                        </div>
                        <div className="d-flex gap-2">
                            <div className="input-group flex-nowrap">
                                <span className="input-group-text search-input"><i className="bi bi-filter"></i></span>
                                <Select
                                    placeholder={t("restaurants.specialties_placeholder")}
                                    isMulti
                                    styles={selectStyles(false)}
                                    components={selectComponents}
                                    noOptionsMessage={() => <span>{t("restaurants.no_options")}</span>}
                                    options={specialties}
                                    closeMenuOnSelect={false}
                                    onChange={(options) => setQuery({...query, specialties: options.map(option => option.value)})}
                                    value={specialties.filter(option => query.specialties.includes(option.value))}
                                />
                            </div>
                            <div className="input-group flex-nowrap">
                                <span className="input-group-text search-input"><i className="bi bi-tags"></i></span>
                                <Select
                                    placeholder={t("restaurants.tags_placeholder")}
                                    isMulti
                                    styles={selectStyles(false)}
                                    components={selectComponents}
                                    noOptionsMessage={() => <span>{t("restaurants.no_options")}</span>}
                                    options={tags}
                                    closeMenuOnSelect={false}
                                    onChange={(options) => setQuery({...query, tags: options.map(option => option.value)})}
                                    value={tags.filter(option => query.tags.includes(option.value))}
                                />
                            </div>
                            <div className="input-group flex-nowrap">
                                <span className="input-group-text search-input"><i className="bi bi-text-left"></i></span>
                                <Select
                                    placeholder={t("restaurants.orderby_placeholder")}
                                    styles={selectStyles(true)}
                                    components={selectComponents}
                                    noOptionsMessage={() => <span>{t("restaurants.no_options")}</span>}
                                    options={orderBy}
                                    onChange={(option) => setQuery({...query, orderBy: option.value})}
                                    value={orderBy.find(option => option.value === query.orderBy)}
                                />
                                <button
                                    className="btn btn-secondary"
                                    type="button"
                                    onClick={() => setQuery({...query, descending: !query.descending})}>
                                    {
                                        query.descending
                                            ?
                                            <i className="bi bi-arrow-up"></i>
                                            :
                                            <i className="bi bi-arrow-down"></i>
                                    }
                                </button>
                            </div>
                        </div>
                    </div>
                    <div className="d-flex flex-column gap-2">
                        <input type="submit" className="btn btn-primary flex-grow-1" value={t("restaurants.search_button")}/>
                        <button className="btn btn-secondary flex-grow-1" onClick={handleClearFilters}>{t("restaurants.clear_filters")}</button>
                    </div>
                </form>
                <div className="restaurants-feed">
                    {
                        isLoading || isError
                            ?
                            new Array(DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
                                return (
                                    <ContentLoader backgroundColor="#eaeaea" foregroundColor="#e0e0e0" width="18rem" height="18rem" key={i}>
                                        <rect x="0" y="0" rx="5" ry="5" width="100%" height="100%"/>
                                    </ContentLoader>
                                );
                            })
                            :
                            (data.pages[0]?.content.length || 0) === 0
                                ?
                                <div className="empty-results">
                                    <h1><i className="bi bi-slash-circle"></i></h1>
                                    <p>{t("restaurants.no_results")}</p>
                                    <button className="btn btn-primary btn-sm" onClick={handleClearFilters}>{t("restaurants.clear_filters")}</button>
                                </div>
                                :
                                data.pages.flatMap(page => page.content).map(restaurant => {
                                    return (
                                        <RestaurantCard
                                            key={restaurant.selfUrl}
                                            restaurantId={1}
                                            name={restaurant.name}
                                            mainImage={restaurant.portrait1Url}
                                            hoverImage={restaurant.portrait2Url}
                                            address={restaurant.address}
                                            rating={restaurant.averageRating}
                                            ratingCount={restaurant.reviewCount}
                                            tags={restaurant.tags}
                                        />
                                    );
                                })
                    }
                    {
                        isFetchingNextPage &&
                        new Array(query.size || DEFAULT_RESTAURANT_COUNT).fill("").map((_, i) => {
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
                {isError && <ErrorModal/>}
            </Page>
        </>
    );
}

export default Restaurants;
