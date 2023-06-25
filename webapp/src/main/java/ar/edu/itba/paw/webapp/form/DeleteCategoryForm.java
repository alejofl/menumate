package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class DeleteCategoryForm {
    @NotNull
    private Long categoryId;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}
