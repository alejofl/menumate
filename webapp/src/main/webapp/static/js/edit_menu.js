function changeInputValue(id, value) {
    document.querySelector(`#${id}`).value = value;
}

document.addEventListener("DOMContentLoaded", () => {
    let addProductFormCategoryId = document.querySelector("#add-product-form-category-id");
    let deleteProductFormProductId = document.querySelector("#delete-product-form-product-id");
    let deleteCategoryFormCategoryId = document.querySelector("#delete-category-form-category-id");
    let editProductPriceFormProductId = document.querySelector("#edit-product-price-form-product-id");
    let deleteEmployeeFormUserId = document.querySelector("#delete-employee-form-user-id");

    // Open Checkout Modal if errors were found
    if (document.querySelector("body").dataset.addProductErrors === "true") {
        document.querySelector(`.add-product-button`).dispatchEvent(new Event("click"));
        addProductFormCategoryId.value = document.querySelector("body").dataset.categoryId;
    } else if (document.querySelector("body").dataset.addCategoryErrors === "true") {
        document.querySelector("#add-category-button").dispatchEvent(new Event("click"));
    } else if (document.querySelector("body").dataset.addEmployeeErrors === "true") {
        document.querySelector("#add-employees-button").dispatchEvent(new Event("click"));
    } else if (document.querySelector("body").dataset.editProductErrors === "true") {
        document.querySelector(`.edit-product-price-button`).dispatchEvent(new Event("click"));
        editProductPriceFormProductId.value = document.querySelector("body").dataset.productId;
    }

    // Modal dismissal
    document.querySelector("#add-item-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-item-modal-name", "");
        changeInputValue("add-item-modal-description", "");
        changeInputValue("add-item-modal-price", "");
        changeInputValue("add-item-modal-image", "");
    });
    document.querySelector("#add-category-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-category-modal-name", "");
    });
    document.querySelector("#employees-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-employee-form-email", "");
    });
    document.querySelector("#edit-item-price-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("edit-product-price-form-price", "");
    });

    document.querySelectorAll(".add-product-button").forEach(element => {
        element.addEventListener("click", (event) => {
            addProductFormCategoryId.value = element.dataset.categoryId;
        });
    });

    document.querySelectorAll(".edit-product-price-button").forEach(element => {
        element.addEventListener("click", (event) => {
            editProductPriceFormProductId.value = element.dataset.productId;
        });
    });

    document.querySelectorAll(".delete-product-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteProductFormProductId.value = element.dataset.productId;
        });
    });

    document.querySelectorAll(".delete-category-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteCategoryFormCategoryId.value = element.dataset.categoryId;
        });
    });

    document.querySelectorAll(".delete-employee-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteEmployeeFormUserId.value = element.dataset.userId;
        });
    });
});