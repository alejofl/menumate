package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class DeleteCategoryForm {
    @NotNull
    private Integer categoryId;

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
}
