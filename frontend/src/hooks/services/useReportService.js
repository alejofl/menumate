import { parseLinkHeader } from "@web3-storage/parse-link-header";
import PagedContent from "../../data/model/PagedContent.js";
import {ACTIVATE_RESTAURANT_CONTENT_TYPE, REPORTS_CONTENT_TYPE} from "../../utils.js";
import Report from "../../data/model/Report.js";

export function useReportService(api) {
    const getReports = async (url, params) => {
        const response = await api.get(
            url,
            {
                params: params,
                headers: {
                    "Accept": REPORTS_CONTENT_TYPE
                }
            }
        );
        const links = parseLinkHeader(response.headers?.link, {});
        const reports = Array.isArray(response.data) ? response.data.map(data => Report.fromJSON(data)) : [];
        return new PagedContent(
            reports,
            links?.first,
            links?.prev,
            links?.next,
            links?.last
        );
    };

    const markAsHandled = async (url) => {
        return await api.patch(url);
    };

    const toggleActiveForRestaurant = async (url, activate) => {
        return await api.patch(
            url,
            {
                activate: activate
            },
            {
                headers: {
                    "Content-Type": ACTIVATE_RESTAURANT_CONTENT_TYPE
                }
            }
        );
    };

    return {
        getReports,
        markAsHandled,
        toggleActiveForRestaurant
    };
}
