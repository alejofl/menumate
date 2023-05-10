package ar.edu.itba.paw.model;

import java.time.LocalDateTime;

public class Review {
    private final OrderItemless order;
    private final int rating;
    private final LocalDateTime date;
    private final String comment;

    public Review(OrderItemless order, int rating, LocalDateTime date, String comment) {
        this.order = order;
        this.rating = rating;
        this.date = date;
        this.comment = comment;
    }

    public OrderItemless getOrder() {
        return order;
    }

    public int getRating() {
        return rating;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
