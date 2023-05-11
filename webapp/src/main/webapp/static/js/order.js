document.addEventListener("DOMContentLoaded", () => {
    let ratingInput = document.querySelector("#review-form-rating");

    document.querySelectorAll(".small-ratings i").forEach(element => {
        let rating = parseInt(element.dataset.number);

        element.addEventListener("click", event => {
            for (let i = 1; i <= rating; i++) {
              document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.add("rating-color");
            }
            for (let i = rating + 1; i <= 5; i++) {
               document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.remove("rating-color");
            }
            ratingInput.value = rating;
        });

        element.addEventListener("mouseover", event => {
            for (let i = 1; i <= rating; i++) {
               document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.add("rating-color");
            }
            for (let i = rating + 1; i <= 5; i++) {
               document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.remove("rating-color");
            }
        });

        element.addEventListener("mouseout", event => {
            for (let i = 1; i <= 5; i++) {
                document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.remove("rating-color");
            }
            for (let i = 1; i <= ratingInput.value; i++) {
               document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.add("rating-color");
            }
        });
    });

    // Modal dismissal
    document.querySelector("#review-modal").addEventListener("hidden.bs.modal", () => {
        ratingInput.value = 0;
        for (let i = 1; i <= 5; i++) {
            document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.remove("rating-color");
        }
        document.querySelector("#create-restaurant-description").value = "";
    });
});