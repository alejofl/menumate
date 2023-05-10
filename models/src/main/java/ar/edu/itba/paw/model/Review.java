package ar.edu.itba.paw.model;

import java.time.LocalDateTime;

public class Review {
    private final OrderItemless order;
    private final int score;
    private final LocalDateTime date;
    private final String comment;

    public Review(OrderItemless order, int score, LocalDateTime date, String comment) {
        this.order = order;
        this.score = score;
        this.date = date;
        this.comment = comment;
    }

    public OrderItemless getOrder() {
        return order;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }
}
