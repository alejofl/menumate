package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.form.CreateReviewForm;
import ar.edu.itba.paw.webapp.form.GetReviewsForm;
import ar.edu.itba.paw.webapp.form.ReviewReplyForm;
import ar.edu.itba.paw.webapp.utils.ControllerUtils;
import ar.edu.itba.paw.webapp.utils.UriUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("reviews")
@Component
public class ReviewController {

    private final ReviewService reviewService;
    private final AccessValidator accessValidator;

    @Context
    private UriInfo uriInfo;

    @Autowired
    public ReviewController(final ReviewService reviewService, final AccessValidator accessValidator) {
        this.reviewService = reviewService;
        this.accessValidator = accessValidator;
    }

    @GET
    public Response getReviews(@Valid @BeanParam final GetReviewsForm getReviewsForm) {
        PaginatedResult<Review> page = reviewService.get(
                getReviewsForm.getUserId(),
                getReviewsForm.getRestaurantId(),
                getReviewsForm.getPageOrDefault(),
                getReviewsForm.getSizeOrDefault(ControllerUtils.DEFAULT_REVIEWS_PAGE_SIZE)
        );

        List<ReviewDto> dtoList = ReviewDto.fromReviewCollection(uriInfo, page.getResult());
        Response.ResponseBuilder builder = Response.ok(new GenericEntity<List<ReviewDto>>(dtoList){});
        return ControllerUtils.addPagingLinks(builder, page, uriInfo).build();
    }

    @GET
    @Path("/{orderId:\\d+}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReviewById(@PathParam("orderId") final long orderId) {
        Review review = reviewService.getByOrder(orderId).orElseThrow(ReviewNotFoundException::new);
        return Response.ok(ReviewDto.fromReview(uriInfo, review)).build();
    }

    @POST
    @PreAuthorize("@accessValidator.checkOrderOwner(#createReviewForm.orderId)")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createReview(@Valid @NotNull final CreateReviewForm createReviewForm) {
        final Review review = reviewService.create(createReviewForm.getOrderId(), createReviewForm.getRating(), createReviewForm.getCommentOrNull());
        return Response.created(UriUtils.getReviewUri(uriInfo, review.getOrderId())).build();
    }

    @PATCH
    @Path("/{orderId:\\d+}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response replyToReviewById(
            @PathParam("orderId") final long orderId,
            @Valid @NotNull final ReviewReplyForm reviewReplyForm
    ) {
        reviewService.replyToReview(orderId, reviewReplyForm.getReply());
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{orderId:\\d+}")
    public Response deleteReviewById(@PathParam("orderId") final long orderId) {
        reviewService.delete(orderId);
        return Response.noContent().build();
    }
}
