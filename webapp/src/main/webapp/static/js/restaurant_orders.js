
document.addEventListener("DOMContentLoaded", () => {
   document.querySelectorAll('.clickable-row').forEach(element => {
      element.addEventListener('click', event => {
          document.querySelector("#order-title").innerHTML = element.dataset.orderId;
          document.querySelector("#order-items").innerHTML = element.dataset.orderItems;
      });
   });
});