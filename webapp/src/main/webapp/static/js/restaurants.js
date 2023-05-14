document.addEventListener("DOMContentLoaded", () => {
    let descendingButton = document.querySelector("#change-descending");
    let descendingInput = document.querySelector("#descending-input");

    if (descendingInput.value === "true") {
        descendingButton.innerHTML = "<i class=\"bi bi-arrow-down\"></i>";
    } else {
        descendingButton.innerHTML = "<i class=\"bi bi-arrow-up\"></i>";
    }

    descendingButton.addEventListener("click", () => {
        if (descendingInput.value === "true") {
            descendingButton.innerHTML = "<i class=\"bi bi-arrow-up\"></i>";
            descendingInput.value = false;
        } else {
            descendingButton.innerHTML = "<i class=\"bi bi-arrow-down\"></i>";
            descendingInput.value = true;
        }
    });
});