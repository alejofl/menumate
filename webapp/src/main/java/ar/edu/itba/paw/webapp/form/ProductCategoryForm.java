package ar.edu.itba.paw.webapp.form;

import javax.validation.constraints.NotNull;

public class ProductCategoryForm {

    @NotNull(message = "{NotNull.ProductCategoryForm.newCategoryId}")
    private long newCategoryId;

    public long getNewCategoryId() {
        return newCategoryId;
    }

    public void setNewCategoryId(long newCategoryId) {
        this.newCategoryId = newCategoryId;
    }
}
