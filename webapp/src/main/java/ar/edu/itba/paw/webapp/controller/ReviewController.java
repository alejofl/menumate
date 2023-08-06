package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.form.CreateReviewForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("reviews")
@Component
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(final ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GET
    @Path("/{orderId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewById(@PathParam("orderId") final long orderId) {
        Review review = reviewService.getByOrder(orderId).orElseThrow(ReviewNotFoundException::new);
        return Response.ok(ReviewDto.fromReview(review)).build();
    }

    @GET
    @Path("/{orderId:\\d+}/reply")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getReviewReply(@PathParam("orderId") final long orderId) {
        Review review = reviewService.getByOrder(orderId).orElseThrow(ReviewNotFoundException::new);
        if (review.getReply() == null)
            return Response.status(Response.Status.NOT_FOUND).build();
        return Response.ok(review.getReply()).build();
    }

    @PUT
    @Path("/{orderId:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReviewById(
            @PathParam("orderId") final long orderId,
            @Valid @NotNull final CreateReviewForm createReviewForm
    ) {
        reviewService.create(orderId, createReviewForm.getRating(), createReviewForm.getCommentOrNull());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{orderId:\\d+}")
    public Response deleteReviewById(@PathParam("orderId") final long orderId) {
        reviewService.delete(orderId);
        return Response.noContent().build();
    }

    @PUT
    @Path("/{orderId:\\d+}/reply")
    @Consumes(MediaType.TEXT_PLAIN)
    public Response createReviewReply(@PathParam("orderId") final long orderId, String reply) {
        reviewService.replyToReview(orderId, reply);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{orderId:\\d+}/reply")
    public Response deleteReviewReply(@PathParam("orderId") final long orderId) {
        reviewService.deleteReviewReply(orderId);
        return Response.noContent().build();
    }
}
