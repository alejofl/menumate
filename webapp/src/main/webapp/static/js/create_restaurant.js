document.addEventListener("DOMContentLoaded", () => {
    let previewImage = document.querySelector(".create-restaurant-preview img");
    let previewTitle = document.querySelector(".create-restaurant-preview .card-body .card-title");
    let previewAddress = document.querySelector(".create-restaurant-preview .card-body .card-text");

    document.querySelector("#create-restaurant-name").addEventListener("change", (event) => {
        previewTitle.innerHTML = event.target.value;
    });

    document.querySelector("#create-restaurant-address").addEventListener("change", (event) => {
        previewAddress.innerHTML = event.target.value;
    });

    document.querySelector("#create-restaurant-portrait1").addEventListener("change", (event) => {
        let url = URL.createObjectURL(event.target.files[0]);
        previewImage.style.setProperty("--main_image", `url(${url})`);
    });

    document.querySelector("#create-restaurant-portrait2").addEventListener("change", (event) => {
        let url = URL.createObjectURL(event.target.files[0]);
        previewImage.style.setProperty("--hover_image", `url(${url})`);
    });
});