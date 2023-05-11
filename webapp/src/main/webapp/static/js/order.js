document.addEventListener("DOMContentLoaded", () => {
    let ratingInput = document.querySelector("#review-form-rating");

    document.querySelectorAll(".small-ratings i").forEach(element => {
       element.addEventListener("click", event => {
          let rating = parseInt(element.dataset.number);
          for (let i = 1; i <= rating; i++) {
              document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.add("rating-color");
          }
           for (let i = rating + 1; i <= 5; i++) {
               document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.remove("rating-color");
           }
           ratingInput.value = rating;
       });
    });
});