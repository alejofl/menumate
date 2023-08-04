package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.model.Review;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ReviewDto {
    private long orderId;
    private int rating;
    private LocalDateTime date;
    private String comment;
    private String reply;

    public static ReviewDto fromReview(final Review review) {
        final ReviewDto dto = new ReviewDto();
        dto.orderId = review.getOrderId();
        dto.rating = review.getRating();
        dto.date = review.getDate();
        dto.comment = review.getComment();
        dto.reply = review.getReply();

        return dto;
    }

    public static List<ReviewDto> fromReviewCollection(final Collection<Review> reviews) {
        return reviews.stream().map(ReviewDto::fromReview).collect(Collectors.toList());
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
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
}
