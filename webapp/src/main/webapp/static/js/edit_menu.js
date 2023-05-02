function changeInputValue(id, value) {
    document.querySelector(`#${id}`).value = value;
}

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