function changeInputValue(id, value) {
    document.querySelector(`#${id}`).value = value;
}

document.addEventListener("DOMContentLoaded", () => {
    let deleteAddressAddressInput = document.querySelector("#delete-address-form-address");

    // Open Checkout Modal if errors were found
    // if (document.querySelector("body").dataset.addProductErrors === "true") {
    //     document.querySelector(`.add-product-button`).dispatchEvent(new Event("click"));
    //     addProductFormCategoryId.value = document.querySelector("body").dataset.categoryId;
    // }

    // Modal dismissal
    // document.querySelector("#add-category-modal").addEventListener("hidden.bs.modal", () => {
    //     changeInputValue("add-category-modal-name", "");
    // });

    document.querySelectorAll(".delete-address-modal-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteAddressAddressInput.value = element.dataset.address;
        });
    });
});