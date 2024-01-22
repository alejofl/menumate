import { parseLinkHeader } from "@web3-storage/parse-link-header";
import PagedContent from "../../data/model/PagedContent.js";
import Review from "../../data/model/Review.js";
import {REVIEW_CONTENT_TYPE} from "../../utils.js";

export function userReviewService(api) {
    const getReviews = async (url, query) => {
        const response = await api.get(
            url,
            {
                params: query,
                headers: {
                    "Accept": REVIEW_CONTENT_TYPE
                }
            }
        );
        const links = parseLinkHeader(response.headers?.link, {});
        const reviews = Array.isArray(response.data) ? response.data.map(data => Review.fromJSON(data)) : [];
        return new PagedContent(
            reviews,
            links?.first,
            links?.prev,
            links?.next,
            links?.last
        );
    };

    return {
        getReviews
    };
}
