package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
}
