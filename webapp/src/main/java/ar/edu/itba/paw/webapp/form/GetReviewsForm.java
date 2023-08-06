package ar.edu.itba.paw.webapp.form;

import javax.ws.rs.QueryParam;

public class GetReviewsForm extends PagingForm {

    @QueryParam("userId")
    private Long userId;

    @QueryParam("restaurantId")
    private Long restaurantId;

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
}
