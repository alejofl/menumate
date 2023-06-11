document.addEventListener("DOMContentLoaded", () => {
    let reviewReplyFormReply = document.querySelector("#reply-review-form-reply");
    let reviewReplyFormOrderId = document.querySelector("#reply-review-form-order-id");

    // Open modal if errors occurred
    if (document.querySelector("body").dataset.reviewReplyFormErrors === "true") {
        new bootstrap.Modal('#reply-review-modal', null).show()
    }

    // Modal Dismissal
    document.querySelector("#reply-review-modal").addEventListener("hidden.bs.modal", () => {
        reviewReplyFormReply.value = "";
    });

    document.querySelectorAll(".reply-review-button").forEach(element => {
        element.addEventListener("click", (event) => {
            reviewReplyFormOrderId.value = element.dataset.orderId;
        });
    });

    document.querySelectorAll(".view-reply-button").forEach(element => {
        element.addEventListener("click", (event) => {
            document.querySelector("#view-reply-container").innerHTML = escapeHtml(element.dataset.reply);
        });
    });


    document.querySelectorAll(".view-order-clickeable").forEach(element => {
        element.addEventListener("click", event => {
            document.querySelector("#order-title").innerHTML = element.dataset.orderId;

            let itemsQuantity = parseInt(element.dataset.orderItemsQuantity);
            let items = [];
            for(let i = 0 ; i < itemsQuantity; i++ ) {
                let item = {
                    lineNumber: element.getAttribute(`data-order-item-${i}-line-number`),
                    comment: element.getAttribute(`data-order-item-${i}-comment`),
                    productName: element.getAttribute(`data-order-item-${i}-product-name`),
                    productPrice: element.getAttribute(`data-order-item-${i}-product-price`),
                    quantity: element.getAttribute(`data-order-item-${i}-quantity`)
                }
                items.push(item)
            }
            document.querySelector("#order-items").innerHTML = ``;
            for(let i = 0 ; i < itemsQuantity; i++ ) {
                document.querySelector("#order-items").innerHTML += `
                <tr>
                    <td class="text-start">${items[i].lineNumber}</td>
                    <td class="text-center">${escapeHtml(items[i].productName)}</td>
                    <td class="text-center">${escapeHtml(items[i].comment)}</td>
                    <td class="text-center">${items[i].quantity}</td>
                    <td class="text-end">$${items[i].productPrice}</td>
                </tr>
              `;
            }

            document.querySelector("#order-details-customer").innerHTML = `${escapeHtml(element.dataset.orderCustomerName)} &lt;<a href="mailto:${escapeHtml(element.dataset.orderCustomerEmail)}">${escapeHtml(element.dataset.orderCustomerEmail)}</a>&gt;`
            document.querySelector("#order-total-price").innerHTML = `$${element.dataset.orderTotalPrice}`;
            document.querySelector("#order-details-table-number").innerHTML = `${element.dataset.orderTableNumber}`;
            document.querySelector("#order-details-address").innerHTML = `${escapeHtml(element.dataset.orderAddress)}`;

            let orderType = parseInt(element.dataset.orderType);
            for (let i = 0; i < 3; i++) {
                document.querySelectorAll(`.order-details-${i}-data`).forEach(e => {
                    if (i !== orderType) {
                        e.classList.replace("d-flex", "d-none");
                    } else {
                        e.classList.replace("d-none", "d-flex");
                    }
                });
            }
        });
    });

});