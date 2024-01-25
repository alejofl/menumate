import { parseLinkHeader } from "@web3-storage/parse-link-header";
import Restaurant from "../../data/model/Restaurant.js";
import PagedContent from "../../data/model/PagedContent.js";
import {
    REPORTS_CONTENT_TYPE,
    RESTAURANT_CATEGORIES_CONTENT_TYPE,
    RESTAURANT_DETAILS_CONTENT_TYPE, RESTAURANT_PRODUCTS_CONTENT_TYPE, RESTAURANT_PROMOTIONS_CONTENT_TYPE,
    RESTAURANTS_CONTENT_TYPE, UNHANDLED_REPORTS_CONTENT_TYPE
} from "../../utils.js";
import Category from "../../data/model/Category.js";
import Product from "../../data/model/Product.js";
import Promotion from "../../data/model/Promotion.js";

export function useRestaurantService(api) {
    const getRestaurants = async (url, query) => {
        const response = await api.get(
            url,
            {
                params: query,
                headers: {
                    "Accept": RESTAURANT_DETAILS_CONTENT_TYPE
                }
            }
        );
        const links = parseLinkHeader(response.headers?.link, {});
        const restaurants = Array.isArray(response.data) ? response.data.map(data => Restaurant.fromJSON(data)) : [];
        return new PagedContent(
            restaurants,
            links?.first,
            links?.prev,
            links?.next,
            links?.last
        );
    };

    const getRestaurant = async (url, details = false) => {
        const response = await api.get(url, {
            headers: {
                "Accept": details ? RESTAURANT_DETAILS_CONTENT_TYPE : RESTAURANTS_CONTENT_TYPE
            }
        });
        return Restaurant.fromJSON(response.data);
    };

    const getCategories = async (url) => {
        const response = await api.get(url, {
            headers: {
                "Accept": RESTAURANT_CATEGORIES_CONTENT_TYPE
            }
        });
        return Array.isArray(response.data) ? response.data.map(data => Category.fromJSON(data)) : [];
    };

    const getProducts = async (url) => {
        const response = await api.get(url, {
            headers: {
                "Accept": RESTAURANT_PRODUCTS_CONTENT_TYPE
            }
        });
        return Array.isArray(response.data) ? response.data.map(data => Product.fromJSON(data)) : [];
    };

    const getPromotions = async (url) => {
        const response = await api.get(url, {
            headers: {
                "Accept": RESTAURANT_PROMOTIONS_CONTENT_TYPE
            }
        });
        return Array.isArray(response.data) ? response.data.map(data => Promotion.fromJSON(data)) : [];
    };

    const getProduct = async (url) => {
        const response = await api.get(url, {
            headers: {
                "Accept": RESTAURANT_PRODUCTS_CONTENT_TYPE
            }
        });
        return Product.fromJSON(response.data);
    };

    const reportRestaurant = async (url, comment) => {
        return await api.post(
            url,
            {
                comment: comment
            },
            {
                headers: {
                    "Content-Type": REPORTS_CONTENT_TYPE
                }
            }
        );
    };

    const createRestaurant = async (
        url,
        imagesUrl,
        name,
        address,
        specialty,
        tags,
        description,
        maxTables,
        logo,
        portrait1,
        portrait2
    ) => {
        const logoId = (await api.postForm(imagesUrl, {image: logo})).data.imageId;
        const portrait1Id = (await api.postForm(imagesUrl, {image: portrait1})).data.imageId;
        const portrait2Id = (await api.postForm(imagesUrl, {image: portrait2})).data.imageId;
        return await api.post(
            url,
            {
                name: name,
                address: address,
                specialty: specialty,
                tags: tags,
                description: description,
                maxTables: maxTables,
                logoId: logoId,
                portrait1Id: portrait1Id,
                portrait2Id: portrait2Id
            },
            {
                headers: {
                    "Content-Type": RESTAURANTS_CONTENT_TYPE
                }
            }
        );
    };

    const getRestaurantsWithUnhandledReports = async (url, query) => {
        const response = await api.get(
            url,
            {
                params: query,
                headers: {
                    "Accept": UNHANDLED_REPORTS_CONTENT_TYPE
                }
            }
        );
        const links = parseLinkHeader(response.headers?.link, {});
        const restaurants = Array.isArray(response.data) ? response.data.map(data => Restaurant.fromJSON(data)) : [];
        return new PagedContent(
            restaurants,
            links?.first,
            links?.prev,
            links?.next,
            links?.last
        );
    };

    const addCategory = async (url, name) => {
        return await api.post(
            url,
            {
                name: name
            },
            {
                headers: {
                    "Content-Type": RESTAURANT_CATEGORIES_CONTENT_TYPE
                }
            }
        );
    };

    return {
        getRestaurants,
        getRestaurant,
        getCategories,
        getProducts,
        getPromotions,
        getProduct,
        reportRestaurant,
        createRestaurant,
        getRestaurantsWithUnhandledReports,
        addCategory
    };
}
