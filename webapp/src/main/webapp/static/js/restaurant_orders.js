document.addEventListener("DOMContentLoaded", () => {
   document.querySelectorAll('.clickable-row').forEach(element => {
      element.addEventListener('click', event => {
          document.querySelector("#order-title").innerHTML = element.dataset.orderId;
          let orderStatus = element.dataset.orderStatus;
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
                    <td>${i}</td>
                    <td>${items[i].productName}</td>
                    <td>${items[i].comment}</td>
                    <td class="text-center">${items[i].quantity}</td>
                    <td>$${items[i].productPrice}</td>
                </tr>
              `;
          }
          document.querySelector("#order-total-price").innerHTML = `$${element.dataset.orderTotalPrice}`;
          let changeStatusButton = document.querySelector("#change-order-button");
          if (orderStatus === 'pending') {
              let url = changeStatusButton.dataset.baseUrl.replace('$1', element.dataset.orderId).replace('$2', `confirm`);
              changeStatusButton.href = url;
              changeStatusButton.innerHTML = `Confirm`;
          } else if (orderStatus === 'confirmed') {
              let url = changeStatusButton.dataset.baseUrl.replace('$1', element.dataset.orderId).replace('$2', `ready`);
              changeStatusButton.href = url;
              changeStatusButton.innerHTML = `Ready`;
          } else {
              let url = changeStatusButton.dataset.baseUrl.replace('$1', element.dataset.orderId).replace('$2', `deliver`);
              changeStatusButton.href = url;
              changeStatusButton.innerHTML = `Deliver`;
          }
      });
   });

});