package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Review;
import ar.edu.itba.paw.webapp.utils.UriUtils;

import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReviewDto {
    private long orderId;
    private String reviewerName;
    private int rating;
    private LocalDateTime date;
    private String comment;
    private String reply;

    private URI selfUrl;
    private URI orderUrl;

    public static ReviewDto fromReview(final UriInfo uriInfo, final Review review) {
        final ReviewDto dto = new ReviewDto();
        dto.orderId = review.getOrderId();
        dto.reviewerName = review.getOrder().getUser().getName();
        dto.rating = review.getRating();
        dto.date = review.getDate();
        dto.comment = review.getComment();
        dto.reply = review.getReply();

        dto.selfUrl = UriUtils.getReviewUri(uriInfo, review.getOrderId());
        dto.orderUrl = UriUtils.getOrderUri(uriInfo, review.getOrderId());

        return dto;
    }

    public static List<ReviewDto> fromReviewCollection(final UriInfo uriInfo, final Collection<Review> reviews) {
        return reviews.stream().map(r -> fromReview(uriInfo, r)).collect(Collectors.toList());
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public URI getSelfUrl() {
        return selfUrl;
    }

    public void setSelfUrl(URI selfUrl) {
        this.selfUrl = selfUrl;
    }

    public URI getOrderUrl() {
        return orderUrl;
    }

    public void setOrderUrl(URI orderUrl) {
        this.orderUrl = orderUrl;
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, reviewerName, rating, date, comment, reply);
    }
}
