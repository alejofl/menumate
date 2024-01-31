/* eslint-disable no-magic-numbers */
import {describe, expect, it} from "vitest";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";
import PagedContent from "../../data/model/PagedContent.js";
import {useReportService} from "../../hooks/services/useReportService.js";
import Report from "../../data/model/Report.js";


describe("useReportService", () => {
    describe("getReports", () => {
        it("should return an instance of PagedContent", async () => {
            const reportService = useReportService(Api);
            const response = await reportService.getReports(apiUrl("/restaurants/1/reports"), {});
            expect(response).toBeInstanceOf(PagedContent);
        });

        it("should return an array of Report as content", async () => {
            const reportService = useReportService(Api);
            const response = await reportService.getReports(apiUrl("/restaurants/1/reports"), {});
            response.content.forEach((report) => expect(report).toBeInstanceOf(Report));
        });

        it("should return correct data", async () => {
            const reportService = useReportService(Api);
            const response = await reportService.getReports(apiUrl("/restaurants/1/reports"), {});
            expect(response.content[0].comment).toEqual(expect.any(String));
            expect(response.content[0].dateReported).toEqual(expect.any(Date));
            expect(response.content[0].handled).toEqual(expect.any(Boolean));
            expect(response.content[0].reportId).toEqual(expect.any(Number));
            expect(response.content[0].restaurantId).toEqual(expect.any(Number));
            expect(response.content[0].restaurantUrl).toEqual(expect.any(String));
            expect(response.content[0].selfUrl).toEqual(expect.any(String));
        });
    });

    describe("markAsHandled", () => {
        it("should return 204 No Content", async () => {
            const reportService = useReportService(Api);
            const response = await reportService.markAsHandled(apiUrl("/restaurants/1/reports/1"), {});
            expect(response.status).toBe(204);
        });
    });

    describe("toggleActiveForRestaurant", () => {
        it("should return 204 No Content", async () => {
            const reportService = useReportService(Api);
            const response = await reportService.toggleActiveForRestaurant(apiUrl("/restaurants/1"), true);
            expect(response.status).toBe(204);
        });
    });
});
