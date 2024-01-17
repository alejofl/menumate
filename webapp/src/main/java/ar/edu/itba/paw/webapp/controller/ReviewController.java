package ar.edu.itba.paw.webapp.controller;

import ar.edu.itba.paw.exception.ReviewNotFoundException;
import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.service.ReviewService;
import ar.edu.itba.paw.util.PaginatedResult;
import ar.edu.itba.paw.webapp.api.CustomMediaType;
import ar.edu.itba.paw.webapp.auth.AccessValidator;
import ar.edu.itba.paw.webapp.dto.ReviewDto;
import ar.edu.itba.paw.webapp.form.GetReviewsForm;
import ar.edu.itba.paw.webapp.form.PostReviewForm;
import ar.edu.itba.paw.webapp.form.PutReviewForm;
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

@Path(UriUtils.REVIEWS_URL)
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
    @Consumes(CustomMediaType.APPLICATION_REVIEW)
    public Response getReviews(@Valid @BeanParam final GetReviewsForm getReviewsForm) {
        PaginatedResult<Review> page = reviewService.get(
                getReviewsForm.getUserId(),
                getReviewsForm.getRestaurantId(),
                getReviewsForm.getPageOrDefault(),
                getReviewsForm.getSizeOrDefault(ControllerUtils.DEFAULT_REVIEWS_PAGE_SIZE)
        );

        List<ReviewDto> dtoList = ReviewDto.fromReviewCollection(uriInfo, page.getResult());
        Response.ResponseBuilder builder = Response.ok(new GenericEntity<List<ReviewDto>>(dtoList) {});
        return ControllerUtils.addPagingLinks(builder, page, uriInfo).build();
    }

    @GET
    @Path("/{orderId:\\d+}")
    @Produces(CustomMediaType.APPLICATION_REVIEW)
    public Response getReviewById(@PathParam("orderId") final long orderId, @Context Request request) {
        final Review review = reviewService.getByOrder(orderId).orElseThrow(ReviewNotFoundException::new);
        final ReviewDto dto = ReviewDto.fromReview(uriInfo, review);
        return ControllerUtils.getResponseUsingEtag(request, dto);
    }

    @POST
    @PreAuthorize("@accessValidator.checkOrderOwner(#reviewForm.orderId)")
    @Consumes(CustomMediaType.APPLICATION_REVIEW)
    public Response postReview(@Valid @NotNull final PostReviewForm reviewForm) {
        final Review review = reviewService.create(reviewForm.getOrderId(), reviewForm.getRating(), reviewForm.getCommentTrimmedOrNull());
        return Response.created(UriUtils.getReviewUri(uriInfo, review.getOrderId())).build();
    }

    @PUT
    @Path("/{orderId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_REVIEW)
    public Response putReview(
            @PathParam("orderId") final long orderId,
            @Valid @NotNull final PutReviewForm reviewForm
    ) {
        final Review review = reviewService.create(orderId, reviewForm.getRating(), reviewForm.getCommentTrimmedOrNull());
        return Response.created(UriUtils.getReviewUri(uriInfo, review.getOrderId())).build();
    }

    @PATCH
    @Path("/{orderId:\\d+}")
    @Consumes(CustomMediaType.APPLICATION_REPLY_REVIEW)
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
