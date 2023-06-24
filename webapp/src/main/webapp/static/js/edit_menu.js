
document.addEventListener("DOMContentLoaded", () => {
    let addProductFormCategoryId = document.querySelector("#add-product-form-category-id");
    let deleteProductFormProductId = document.querySelector("#delete-product-form-product-id");
    let deleteCategoryFormCategoryId = document.querySelector("#delete-category-form-category-id");
    let deleteEmployeeFormUserId = document.querySelector("#delete-employee-form-user-id");

    let editProductFormName = document.querySelector("#edit-item-modal-name");
    let editProductFormDescription = document.querySelector("#edit-item-modal-description");
    let editProductFormPrice = document.querySelector("#edit-item-modal-price");
    let editProductFormImage = document.querySelector("#edit-item-modal-image");

    let addProductNamePreview = document.querySelector("#add-item-modal-name-preview");
    let addProductDescriptionPreview = document.querySelector("#add-item-modal-description-preview");
    let addProductPricePreview = document.querySelector("#add-item-modal-price-preview");
    let addProductImagePreview = document.querySelector("#add-item-modal-image-preview");

    let editProductNamePreview = document.querySelector("#edit-item-modal-name-preview");
    let editProductDescriptionPreview = document.querySelector("#edit-item-modal-description-preview");
    let editProductPricePreview = document.querySelector("#edit-item-modal-price-preview");
    let editProductImagePreview = document.querySelector("#edit-item-modal-image-preview");

    // Open Checkout Modal if errors were found
    if (document.querySelector("body").dataset.addProductErrors === "true") {
        document.querySelector(`.add-product-button`).dispatchEvent(new Event("click"));
        addProductFormCategoryId.value = document.querySelector("body").dataset.categoryId;
    } else if (document.querySelector("body").dataset.addCategoryErrors === "true") {
        document.querySelector("#add-category-button").dispatchEvent(new Event("click"));
    } else if (document.querySelector("body").dataset.addEmployeeErrors === "true") {
        document.querySelector("#add-employees-button").dispatchEvent(new Event("click"));
    } else if (document.querySelector("body").dataset.editProductErrors === "true") {
        editProductNamePreview.innerHTML = editProductFormName.value;
        editProductDescriptionPreview.innerHTML = editProductFormDescription.value;
        editProductPricePreview.innerHTML = editProductFormPrice.value;

        let productId = document.querySelector("body").dataset.productId;
        let imageId = document.querySelector(`.edit-product-button[data-product-id="${productId}"]`).dataset.imageId;
        editProductImagePreview.src = window.location.href.replace(/restaurants\/\d+\/products\/edit/, "images/" + imageId);

        new bootstrap.Modal('#edit-item-modal', null).show()
    } else if (document.querySelector("body").dataset.editCategoryErrors === "true") {
        new bootstrap.Modal('#edit-category-modal', null).show()
    } else if (document.querySelector("body").dataset.editInformationErrors === "true") {
        new bootstrap.Modal('#edit-information-modal', null).show()
    } else if (document.querySelector("body").dataset.createPromotionErrors === "true") {
        new bootstrap.Modal('#create-promotion-modal', null).show()
    }

    // Modal dismissal
    document.querySelector("#add-item-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-item-modal-name", "");
        changeInputValue("add-item-modal-description", "");
        changeInputValue("add-item-modal-price", "");
        changeInputValue("add-item-modal-image", "");
        addProductNamePreview.innerHTML = addProductNamePreview.dataset.default;
        addProductDescriptionPreview.innerHTML = addProductDescriptionPreview.dataset.default;
        addProductPricePreview.innerHTML = addProductPricePreview.dataset.default;
        addProductImagePreview.src = addProductImagePreview.dataset.default;
    });
    document.querySelector("#add-category-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-category-modal-name", "");
    });
    document.querySelector("#employees-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-employee-form-email", "");
        document.querySelectorAll("div[id^='edit-employee-'][id$='-edit-enabled']").forEach(element => {
            element.style.display = "none";
        });
        document.querySelectorAll("div[id^='edit-employee-'][id$='-edit-disabled']").forEach(element => {
            element.style.display = "block";
        });
        document.querySelectorAll("div[id^='edit-employee-'][id$='-edit-enabled-button']").forEach(element => {
            element.style.display = "none";
        });
        document.querySelectorAll("div[id^='edit-employee-'][id$='-edit-disabled-button']").forEach(element => {
            element.style.display = "block";
        });
    });
    document.querySelector("#create-promotion-modal").addEventListener("hidden.bs.modal", () => {
        changeInputValue("create-promotion-modal-percentage", "");
        changeInputValue("create-promotion-modal-start-date-time", "");
        changeInputValue("create-promotion-modal-end-date-time", "");
        changeInputValue("create-promotion-modal-source-product-id", "");
    });

    document.querySelectorAll(".add-product-button").forEach(element => {
        element.addEventListener("click", (event) => {
            addProductFormCategoryId.value = element.dataset.categoryId;
        });
    });

    document.querySelectorAll(".edit-product-button").forEach(element => {
        element.addEventListener("click", (event) => {
            editProductFormName.value = element.dataset.productName;
            editProductFormDescription.value = element.dataset.description;
            editProductFormPrice.value = element.dataset.productPrice;
            document.querySelector("#edit-item-modal-category").value = element.dataset.categoryId;
            document.querySelector("#edit-item-modal-product-id").value = element.dataset.productId;

            editProductNamePreview.innerHTML = element.dataset.productName;
            editProductDescriptionPreview.innerHTML = element.dataset.description;
            editProductPricePreview.innerHTML = element.dataset.productPrice;
            editProductImagePreview.src = window.location.href.replace(/restaurants\/\d+\/edit/, "images/" + element.dataset.imageId)
        });
    });

    document.querySelectorAll(".delete-product-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteProductFormProductId.value = element.dataset.productId;
        });
    });

    document.querySelectorAll(".stop-promotion-button").forEach(element => {
        element.addEventListener("click", (event) => {
            document.querySelector("#stop-promotion-form-product-id").value = element.dataset.productId;
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

    // Edit Employee Role
    document.querySelectorAll(".edit-employee-button").forEach(element => {
        let userId = element.dataset.userId;
        element.addEventListener("click", (event) => {
            document.querySelector(`#edit-employee-${userId}-edit-disabled-button`).style.display = "none";
            document.querySelector(`#edit-employee-${userId}-edit-enabled-button`).style.display = "block";
            document.querySelector(`#edit-employee-${userId}-edit-disabled`).style.display = "none";
            document.querySelector(`#edit-employee-${userId}-edit-enabled`).style.display = "block";
        });
    });

    document.querySelectorAll(".save-employee-button").forEach(element => {
        let userId = element.dataset.userId;
        element.addEventListener("click", (event) => {
            document.querySelector(`#edit-employee-${userId}-edit-enabled form`).submit();
        });
    });

    document.querySelectorAll(".edit-category-button").forEach(element => {
        element.addEventListener("click", (event) => {
            document.querySelector("#edit-category-modal-category").value = element.dataset.categoryId;
            document.querySelector("#edit-category-modal-name").value = element.dataset.categoryName;
        });
    });

    document.querySelectorAll(".create-promotion-button").forEach(element => {
        element.addEventListener("click", (event) => {
            document.querySelector("#create-promotion-modal-source-product-id").value = element.dataset.productId;
        });
    });

    // Preview Product on Addition
    let nameInput = document.querySelector("#add-item-modal-name");
    let descriptionInput = document.querySelector("#add-item-modal-description");
    let priceInput = document.querySelector("#add-item-modal-price");
    let imageInput = document.querySelector("#add-item-modal-image");

    nameInput.addEventListener("change", () => {
        addProductNamePreview.innerHTML = nameInput.value;
    });
    descriptionInput.addEventListener("change", () => {
        addProductDescriptionPreview.innerHTML = descriptionInput.value;
    });
    priceInput.addEventListener("change", () => {
        addProductPricePreview.innerHTML = priceInput.value;
    });
    imageInput.addEventListener("change", (event) => {
        addProductImagePreview.src = URL.createObjectURL(event.target.files[0]);
    });

    editProductFormName.addEventListener("change", () => {
        editProductNamePreview.innerHTML = editProductFormName.value;
    });
    editProductFormDescription.addEventListener("change", () => {
        editProductDescriptionPreview.innerHTML = editProductFormDescription.value;
    });
    editProductFormPrice.addEventListener("change", () => {
        editProductPricePreview.innerHTML = editProductFormPrice.value;
    });
    editProductFormImage.addEventListener("change", (event) => {
        editProductImagePreview.src = URL.createObjectURL(event.target.files[0]);
    });

    let promotionInstantInputs = document.querySelectorAll(".promotion-instant-duration");
    let promotionInstantDays = document.querySelector("#promotion-instant-days");
    let promotionInstantHours = document.querySelector("#promotion-instant-hours");
    let promotionInstantMinutes = document.querySelector("#promotion-instant-minutes");

    // Prevent non-number characters in Quantity input
    promotionInstantInputs.forEach(element => {
        element.addEventListener("keypress", (event) => {
            if (event.key.length !== 1) {
                return true;
            }
            if (event.target.value.length > 0) {
                if ("0123456789".indexOf(event.key) === -1) {
                    event.preventDefault();
                    return false;
                }
            } else {
                if ("123456789".indexOf(event.key) === -1) {
                    event.preventDefault();
                    return false;
                }
            }
        });
    });

    promotionInstantMinutes.addEventListener("change", (event) => {
        let value = parseInt(event.target.value);
        if (value >= 60) {
            let hours = Math.floor(value / 60);
            promotionInstantMinutes.value = value % 60;
            promotionInstantHours.value = (promotionInstantHours.value === "" ? 0 : parseInt(promotionInstantHours.value)) + hours;
            promotionInstantHours.dispatchEvent(new Event("change"));
        }
    });
    promotionInstantHours.addEventListener("change", (event) => {
        let value = parseInt(event.target.value);
        if (value >= 24) {
            let days = Math.floor(value / 24);
            promotionInstantHours.value = value % 24;
            promotionInstantDays.value = (promotionInstantDays.value === "" ? 0 : parseInt(promotionInstantDays.value)) + days;
        }
    })



    // Promotion Type: 0 -> instant ; 1 -> scheduled
    let promotionType = document.querySelector("#create-promotion-modal-promotion-type");
    document.querySelector("#promotion-instant-tab").addEventListener("click", () => {
        promotionType.value = 0;
    });
    document.querySelector("#promotion-scheduled-tab").addEventListener("click", () => {
        promotionType.value = 1;
    });
});