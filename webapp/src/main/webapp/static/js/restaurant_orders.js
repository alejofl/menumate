
document.addEventListener("DOMContentLoaded", () => {
   document.querySelectorAll('.clickable-row').forEach(element => {
      element.addEventListener('click', event => {
          document.querySelector("#order-title").innerHTML = `Order ID: ` + element.dataset.orderId;
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
          document.querySelector("#order-total-price").innerHTML = `Total Price: $` + element.dataset.orderTotalPrice;
          console.log(items)
      });
   });
});
