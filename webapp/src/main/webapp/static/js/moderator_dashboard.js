document.addEventListener("DOMContentLoaded", () => {
    let deleteModeratorFormUserId = document.querySelector("#delete-moderator-form-user-id");

    if (document.querySelector("body").dataset.addModeratorFormErrors === "true") {
        new bootstrap.Modal('#moderators-modal', null).show();
    }

    document.querySelectorAll(".delete-moderator-button").forEach(element => {
        element.addEventListener("click", (event) => {
            deleteModeratorFormUserId.value = element.dataset.userId;
        });
    });
});