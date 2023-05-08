document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("form[method='post']").forEach(form => {
        form.addEventListener("submit", event => {
            form.querySelectorAll("button[type='submit'], input[type='submit']").forEach(button => {
                button.disabled = true;
            });
        });
    });
});