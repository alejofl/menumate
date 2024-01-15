package ar.edu.itba.paw.webapp.form;

import ar.edu.itba.paw.webapp.form.validation.BothFieldsNull;

import javax.validation.constraints.Size;

@BothFieldsNull(allowBothNull = false, allowBothNotNull = true, field1Name = "name", field2Name = "orderNum", message = "Must specify a name or an orderNum")
public class UpdateCategoryForm {
    @Size(max = 50)
    private String name;

    private Integer orderNum;

    public String getName() {
        return name;
    }

    public String getNameTrimmed() {
        if (name == null)
            return null;

        String trimmed = name.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }
}
