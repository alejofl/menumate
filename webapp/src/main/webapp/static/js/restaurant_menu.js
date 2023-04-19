let cartIndex = 0;
let cart = [];

function changeInputValue(id, value) {
    document.querySelector(`#${id}`).value = value;
}

document.addEventListener("DOMContentLoaded", () => {
    let cartModalHeader = document.querySelector("#add-item-to-cart-header");
    let cartModalTitle = document.querySelector("#add-item-to-cart-title");
    let cartModalDescription = document.querySelector("#add-item-to-cart-description");
    let cartModalButton = document.querySelector("#add-item-to-cart-add");
    let cartItemsContainer = document.querySelector("#checkout-cart-items");

    // Fill modal for every menu item
    document.querySelectorAll(".menu-item-card-button").forEach((value) => {
        value.addEventListener("click", () => {
            cartModalHeader.style.setProperty("--image", `url(${value.dataset.infoImage})`);
            cartModalTitle.innerHTML = value.dataset.infoTitle;
            cartModalDescription.innerHTML = value.dataset.infoDescription;
            cartModalButton.setAttribute("data-info-unit-price", value.dataset.infoPrice);
            cartModalButton.setAttribute("data-info-title", value.dataset.infoTitle);
            cartModalButton.setAttribute("data-info-id", value.dataset.infoId);
            cartModalButton.innerHTML = `Add Item to Cart ($${value.dataset.infoPrice})`;

        });
    });

    let cartModalQuantity = document.querySelector("#add-item-to-cart-quantity");
    let cartModalComments = document.querySelector("#add-item-to-cart-comments");

    // Prevent non-number characters in Quantity input
    cartModalQuantity.addEventListener("keypress", (event) => {
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

    // Plus and Minus buttons of Modal
    document.querySelector("#add-item-to-cart-minus").addEventListener("click", () => {
        if (cartModalQuantity.value >= 2) {
            cartModalQuantity.value--;
            let price = parseFloat(cartModalButton.dataset.infoUnitPrice);
            cartModalButton.innerHTML = `Add Item to Cart ($${price * cartModalQuantity.value})`
        }
    });
    document.querySelector("#add-item-to-cart-plus").addEventListener("click", () => {
        cartModalQuantity.value++;
        let price = parseFloat(cartModalButton.dataset.infoUnitPrice);
        cartModalButton.innerHTML = `Add Item to Cart ($${price * cartModalQuantity.value})`
    });

    // Modal dismissal
    document.querySelector("#add-item-to-cart").addEventListener("hidden.bs.modal", () => {
        changeInputValue("add-item-to-cart-quantity", 1);
        changeInputValue("add-item-to-cart-comments", "");
    });
    document.querySelector("#checkout").addEventListener("hidden.bs.modal", () => {
        changeInputValue("checkout-name", "");
        changeInputValue("checkout-email", "");
        changeInputValue("checkout-table-number", "");
        changeInputValue("checkout-address", "");
    });

    // Add item to cart
    cartModalButton.addEventListener("click", (event) => {
        let item = {
            "title": event.target.dataset.infoTitle,
            "quantity": cartModalQuantity.value,
            "price": cartModalQuantity.value * parseFloat(event.target.dataset.infoUnitPrice),
            "comments": cartModalComments.value
        };
        cartItemsContainer.innerHTML += `
            <input type="hidden" name="cart[${cartIndex}].productId" value="${event.target.dataset.infoId}"/>
            <input type="hidden" name="cart[${cartIndex}].quantity" value="${parseInt(cartModalQuantity.value)}"/>
            <input type="hidden" name="cart[${cartIndex}].comment" value="${cartModalComments.value}"/>
        `;
        cartIndex++;
        cart.push(item);
        document.querySelector("#cart-container").innerHTML += `
            <li class="list-group-item">
                <div class="cart-item">
                    <div class="cart-item-body">
                        <span class="badge text-bg-secondary">x${item.quantity}</span>
                        <span>${item.title}</span>
                    </div>
                    <span><strong>$${item.price}</strong></span>
                </div>
            </li>
        `;
        document.querySelector("#place-order-button").disabled = false;
    });

    // Place Order Price
    document.querySelector("#place-order-button").addEventListener("click", (event) => {
        let price = cart.reduce(function (result, item) {
            return result + item.price;
        }, 0);
        document.querySelector(`#checkout-button`).value = `Place Order ($${price})`;
    });

    // Order Type Selector (FIXME this values are hardcoded)
    document.querySelector("#checkout-dinein-tab").addEventListener("click", () => {
        document.querySelector("#checkout-order-type").value = 0;
    });
    document.querySelector("#checkout-takeaway-tab").addEventListener("click", () => {
        document.querySelector("#checkout-order-type").value = 1;
    });
    document.querySelector("#checkout-delivery-tab").addEventListener("click", () => {
        document.querySelector("#checkout-order-type").value = 2;
    });

    // Auto-Scroll
    document.querySelectorAll(".category-item").forEach((value) => {
        value.addEventListener("click", () => {
            document.querySelector(`#category-${value.dataset.category}`).scrollIntoView({block: "center"});
            let previousActive = document.querySelector(".category-item.active");
            if (previousActive) {
                previousActive.classList.remove("active");
            }
            value.classList.add("active");
        });
    });
});