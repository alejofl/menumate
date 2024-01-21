import { parseLinkHeader } from "@web3-storage/parse-link-header";
import Restaurant from "../../data/model/Restaurant.js";
import PagedContent from "../../data/model/PagedContent.js";
import {
    REPORTS_CONTENT_TYPE,
    RESTAURANT_CATEGORIES_CONTENT_TYPE,
    RESTAURANT_DETAILS_CONTENT_TYPE, RESTAURANT_PRODUCTS_CONTENT_TYPE, RESTAURANT_PROMOTIONS_CONTENT_TYPE,
    RESTAURANTS_CONTENT_TYPE
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
        console.log(url + "NADA");
        console.log("ENTRE");
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

    return {
        getRestaurants,
        getRestaurant,
        getCategories,
        getProducts,
        getPromotions,
        getProduct,
        reportRestaurant
    };
}
