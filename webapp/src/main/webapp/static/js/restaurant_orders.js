function selectOrderTypeTab(tab) {
    // document.querySelector(`#orders-${tab}-table`).classList.add("active");
    document.querySelector(`#orders-${tab}`).classList.add("active", "show");
}

document.addEventListener("DOMContentLoaded", () => {
   document.querySelectorAll('.clickable-row').forEach(element => {
      element.addEventListener('click', event => {
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
                    <td>${i}</td>
                    <td>${items[i].productName}</td>
                    <td>${items[i].comment}</td>
                    <td class="text-center">${items[i].quantity}</td>
                    <td>$${items[i].productPrice}</td>
                </tr>
              `;
          }
          document.querySelector("#order-total-price").innerHTML = `$${element.dataset.orderTotalPrice}`;
      });
   });

    let orderStatus = document.querySelector("#order-status");
    // Select Order Type tab automatically
    if (orderStatus.value === "PENDING" || orderStatus.value === "") {
        selectOrderTypeTab("ordered");
        orderStatus.value = "PENDING"
    } else if (orderStatus.value === "CONFIRMED") {
        selectOrderTypeTab("confirmed");
    } else {
        selectOrderTypeTab("ready");
    }

    // Order Type Selector (FIXME this values are hardcoded)
    document.querySelector("#orders-ordered").addEventListener("click", () => {
        orderStatus.value = 'PENDING';
        console.log(orderStatus.value);
    });
    document.querySelector("#orders-confirmed").addEventListener("click", () => {
        orderStatus.value = 'CONFIRMED';
        console.log(orderStatus.value);
    });
    document.querySelector("#orders-ready").addEventListener("click", () => {
        orderStatus.value = 'READY';
        console.log(orderStatus.value);
    });
});