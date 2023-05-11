function addRating(end) {
    for (let i = 1; i <= end; i++) {
        document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.add("rating-color");
    }
}

function removeRating(start) {
    for (let i = start; i <= 5; i++) {
        document.querySelector(`.small-ratings i[data-number="${i}"]`).classList.remove("rating-color");
    }
}

document.addEventListener("DOMContentLoaded", () => {
    let ratingInput = document.querySelector("#review-form-rating");
    addRating(parseInt(ratingInput.value));

    if (document.querySelector("body").dataset.error === "true") {
        document.querySelector("#review-modal-button").dispatchEvent(new Event("click"));
    }

    document.querySelectorAll(".small-ratings i").forEach(element => {
        let rating = parseInt(element.dataset.number);

        element.addEventListener("click", event => {
            addRating(rating);
            removeRating(rating + 1);
            ratingInput.value = rating;
        });

        element.addEventListener("mouseover", event => {
            addRating(rating);
            removeRating(rating + 1);
        });

        element.addEventListener("mouseout", event => {
            removeRating(1);
            addRating(ratingInput.value);
        });
    });

    // Modal dismissal
    document.querySelector("#review-modal").addEventListener("hidden.bs.modal", () => {
        ratingInput.value = "";
        removeRating(1);
        document.querySelector("#create-restaurant-description").value = "";
    });
});