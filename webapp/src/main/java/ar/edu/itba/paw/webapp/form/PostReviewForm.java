package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class PostReviewForm extends PutReviewForm {

    @NotNull
    private Long orderId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
