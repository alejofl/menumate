function escapeHtml(str) {
    let div = document.createElement('div');
    div.appendChild(document.createTextNode(str));
    return div.innerHTML;
}

document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("form[method='post']").forEach(form => {
        form.addEventListener("submit", event => {
            form.querySelectorAll("button[type='submit'], input[type='submit']").forEach(button => {
                button.disabled = true;
            });
        });
    });
});