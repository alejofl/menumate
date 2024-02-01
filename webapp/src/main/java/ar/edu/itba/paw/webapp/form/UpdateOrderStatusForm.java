package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.model.OrderStatus;
import ar.edu.itba.paw.webapp.form.validation.EnumMessageCode;
import org.hibernate.validator.constraints.NotBlank;

public class UpdateOrderStatusForm {

    @NotBlank(message = "{NotBlank.UpdateOrderStatusForm.status}")
    @EnumMessageCode(enumClass = OrderStatus.class, message = "{EnumMessageCode.UpdateOrderStatus.status}")
    private String status;

    public String getStatus() {
        return status;
    }

    public OrderStatus getStatusAsEnum() {
        return OrderStatus.fromCode(status);
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
