document.addEventListener("DOMContentLoaded", () => {
    let addProductFormCategoryId = document.querySelector("#add-product-form-category-id");
    let deleteProductFormProductId = document.querySelector("#delete-product-form-product-id");
    let deleteCategoryFormCategoryId = document.querySelector("#delete-category-form-category-id");

    let addProductModal = document.querySelector("#add-item-modal");
    let addCategoryModal = document.querySelector("#add-category-modal");

    // Open Checkout Modal if errors were found
    if (document.querySelector("body").dataset.addProductErrors === "true") {
        addProductFormCategoryId.value = document.querySelector("body").dataset.categoryId;
        document.querySelector(`.add-product-button`).dispatchEvent(new Event("click"));
    } else if (document.querySelector("body").dataset.addCategoryErrors === "true") {
        document.querySelector("#add-category-button").dispatchEvent(new Event("click"));
    }

    document.querySelectorAll(".add-product-button").forEach(element => {
        element.addEventListener("click", (event) => {
            addProductFormCategoryId.value = element.dataset.categoryId;
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
});