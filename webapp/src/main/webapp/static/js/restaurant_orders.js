document.addEventListener("DOMContentLoaded", () => {
   document.querySelectorAll(".clickable-row").forEach(element => {
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
                    <td class="text-start">${i}</td>
                    <td class="text-center">${items[i].productName}</td>
                    <td class="text-center">${items[i].comment}</td>
                    <td class="text-center">${items[i].quantity}</td>
                    <td class="text-end">$${items[i].productPrice}</td>
                </tr>
              `;
          }

          document.querySelector("#order-details-customer").innerHTML = `${element.dataset.orderCustomerName} &lt;<a href="mailto:${element.dataset.orderCustomerEmail}">${element.dataset.orderCustomerEmail}</a>&gt;`
          document.querySelector("#order-total-price").innerHTML = `$${element.dataset.orderTotalPrice}`;
          document.querySelector("#order-details-table-number").innerHTML = `${element.dataset.orderTableNumber}`;
          document.querySelector("#order-details-address").innerHTML = `${element.dataset.orderAddress}`;

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

          let orderStatus = element.dataset.orderStatus;
          let changeStatusButton = document.querySelector("#change-order-button");
          if (orderStatus === "0") {
              let url = changeStatusButton.dataset.baseUrl.replace("$1", element.dataset.orderId).replace("$2", `confirm`);
              changeStatusButton.href = url;
              changeStatusButton.innerHTML = `Confirm`;
          } else if (orderStatus === "1") {
              let url = changeStatusButton.dataset.baseUrl.replace("$1", element.dataset.orderId).replace("$2", `ready`);
              changeStatusButton.href = url;
              changeStatusButton.innerHTML = `Ready`;
          } else if (orderStatus === "2") {
              let url = changeStatusButton.dataset.baseUrl.replace("$1", element.dataset.orderId).replace("$2", `deliver`);
              changeStatusButton.href = url;
              changeStatusButton.innerHTML = `Deliver`;
          } else {
              changeStatusButton.remove();
          }
      });
   });

});