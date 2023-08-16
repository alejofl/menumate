package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.BothFieldsNull;

import javax.ws.rs.QueryParam;

@BothFieldsNull(allowBothNull = true, allowBothNotNull = false, field1Name = "restaurantId", field2Name = "userId")
public class GetRestaurantRolesForm extends PagingForm {

    @QueryParam("restaurantId")
    private Long restaurantId;

    @QueryParam("userId")
    private Long userId;

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
