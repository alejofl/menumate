package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public class GetOrdersForm extends PagingForm {

    @QueryParam("userId")
    private Long userId;

    @QueryParam("restaurantId")
    private Long restaurantId;

    @QueryParam("status")
    @EnumMessageCode(enumClass = OrderStatus.class, message = "{EnumMessageCode.GetOrdersForm.status}")
    private String status;

    @QueryParam("descending")
    @DefaultValue("false")
    private boolean descending;

    @QueryParam("inProgress")
    @DefaultValue("false")
    private boolean inProgress;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getStatus() {
        return status;
    }

    public OrderStatus getStatusAsEnum() {
        return status == null ? null : OrderStatus.fromCode(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean getDescending() {
        return descending;
    }

    public void setDescending(boolean descending) {
        this.descending = descending;
    }

    public boolean getInProgress() {
        return inProgress;
    }

    public void setInProgress(boolean inProgress) {
        this.inProgress = inProgress;
    }
}
