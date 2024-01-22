package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class PostReviewForm extends PutReviewForm {

    @NotNull(message = "{NotNull.PostReviewForm.orderId}")
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
