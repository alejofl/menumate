/* eslint-disable no-magic-numbers */
import {describe, expect, it} from "vitest";
import Api from "../../data/Api.js";
import {apiUrl} from "../setup/utils.js";
import PagedContent from "../../data/model/PagedContent.js";
import {useReviewService} from "../../hooks/services/useReviewService.js";
import Review from "../../data/model/Review.js";


describe("useReviewService", () => {
    describe("getReviews", () => {
        it("should return an instance of PagedContent", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.getReviews(apiUrl("/reviews"), {});
            expect(response).toBeInstanceOf(PagedContent);
        });

        it("should return an array of Reviews as content", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.getReviews(apiUrl("/reviews"), {});
            response.content.forEach((review) => expect(review).toBeInstanceOf(Review));
        });

        it("should return correct data", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.getReviews(apiUrl("/reviews"), {});
            expect(response.content[0].comment).toEqual(expect.any(String));
            expect(response.content[0].date).toEqual(expect.any(Date));
            expect(response.content[0].orderId).toEqual(expect.any(Number));
            expect(response.content[0].orderUrl).toEqual(expect.any(String));
            expect(response.content[0].rating).toEqual(expect.any(Number));
            expect(response.content[0].reviewerName).toEqual(expect.any(String));
            expect(response.content[0].reply).toEqual(expect.any(String));
            expect(response.content[0].selfUrl).toEqual(expect.any(String));
        });
    });

    describe("replyReview", () => {
        it("should return 204 No Content", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.replyReview(apiUrl("/reviews/1"), {});
            expect(response.status).toBe(204);
        });
    });

    describe("getReview", () => {
        it("should return an instance of Review", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.getReview(apiUrl("/reviews/1"), {});
            expect(response).toBeInstanceOf(Review);
        });

        it("should return correct data", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.getReview(apiUrl("/reviews/1"), {});
            expect(response.comment).toEqual(expect.any(String));
            expect(response.date).toEqual(expect.any(Date));
            expect(response.orderId).toEqual(expect.any(Number));
            expect(response.orderUrl).toEqual(expect.any(String));
            expect(response.rating).toEqual(expect.any(Number));
            expect(response.reviewerName).toEqual(expect.any(String));
            expect(response.reply).toEqual(expect.any(String));
            expect(response.selfUrl).toEqual(expect.any(String));
        });
    });

    describe("makeReview", () => {
        it("should return 201 Created", async () => {
            const reviewService = useReviewService(Api);
            const response = await reviewService.makeReview(apiUrl("/reviews"), {});
            expect(response.status).toBe(201);
        });
    });
});
