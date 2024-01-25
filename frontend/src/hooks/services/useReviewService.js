import { parseLinkHeader } from "@web3-storage/parse-link-header";
import PagedContent from "../../data/model/PagedContent.js";
import Review from "../../data/model/Review.js";
import {REVIEW_CONTENT_TYPE, REVIEW_REPLY_CONTENT_TYPE} from "../../utils.js";

export function useReviewService(api) {
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

    const replyReview = async (url, reply) => {
        return await api.patch(
            url,
            {
                reply: reply
            },
            {
                headers: {
                    "Content-Type": REVIEW_REPLY_CONTENT_TYPE
                }
            }
        );
    };

    const getReview = async (url) => {
        const response = await api.get(
            url,
            {
                headers: {
                    "Accept": REVIEW_CONTENT_TYPE
                }
            }
        );
        return Review.fromJSON(response.data);
    };

    const makeReview = async (url, orderId, rating, comment) => {
        return await api.post(
            url,
            {
                orderId: orderId,
                rating: rating,
                comment: comment
            },
            {
                headers: {
                    "Content-Type": REVIEW_CONTENT_TYPE
                }
            }
        );
    };

    return {
        getReviews,
        replyReview,
        getReview,
        makeReview
    };
}
