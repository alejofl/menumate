document.addEventListener("DOMContentLoaded", () => {
    let deleteAddressAddressInput = document.querySelector("#delete-address-form-address");
    let addAddressAddressInput = document.querySelector("#add-address-form-address");
    let addAddressNameInput = document.querySelector("#add-address-form-name");

    // Open Checkout Modal if errors were found
    if (document.querySelector("body").dataset.addAddressError === "true") {
        document.querySelector("#add-address-button").dispatchEvent(new Event("click"));
    }

    // Modal dismissal
    document.querySelector("#add-address-modal").addEventListener("hidden.bs.modal", () => {
        addAddressNameInput.value = "";
        addAddressAddressInput.value = "";
        addAddressAddressInput.removeAttribute("disabled");
        addAddressAddressInput.removeAttribute("readonly");
    });

    document.querySelectorAll(".delete-address-modal-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteAddressAddressInput.value = element.dataset.address;
        });
    });

    document.querySelectorAll(".add-address-modal-button").forEach(element => {
        element.addEventListener("click", (event) => {
            addAddressAddressInput.value = element.dataset.address;
            addAddressAddressInput.setAttribute("disabled", "");
            addAddressAddressInput.setAttribute("readonly", "");
        });
    });
});