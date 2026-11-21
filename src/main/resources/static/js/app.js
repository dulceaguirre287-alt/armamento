document.addEventListener("DOMContentLoaded", () => {
    const shell = document.querySelector("[data-app-shell]");
    const toggle = document.querySelector("[data-sidebar-toggle]");
    const backdrop = document.querySelector("[data-sidebar-backdrop]");

    const closeMenu = () => shell?.classList.remove("app-shell--menu-open");

    toggle?.addEventListener("click", () => {
        if (window.matchMedia("(max-width: 63.99rem)").matches) {
            shell?.classList.toggle("app-shell--menu-open");
        } else {
            shell?.classList.toggle("app-shell--collapsed");
            localStorage.setItem(
                "armamento-sidebar-collapsed",
                shell.classList.contains("app-shell--collapsed") ? "true" : "false"
            );
        }
    });

    if (shell && window.matchMedia("(min-width: 64rem)").matches
        && localStorage.getItem("armamento-sidebar-collapsed") === "true") {
        shell.classList.add("app-shell--collapsed");
    }

    backdrop?.addEventListener("click", closeMenu);
    document.addEventListener("keydown", (event) => {
        if (event.key === "Escape") {
            closeMenu();
            document.querySelectorAll("dialog[open]").forEach((dialog) => dialog.close());
        }
    });

    document.querySelectorAll("[data-confirm]").forEach((form) => {
        form.addEventListener("submit", (event) => {
            const prompt = form.dataset.confirm || "¿Confirmas esta acción?";
            if (!window.confirm(prompt)) {
                event.preventDefault();
            }
        });
    });

    document.querySelectorAll("[data-modal-open]").forEach((button) => {
        button.addEventListener("click", () => {
            document.getElementById(button.dataset.modalOpen)?.showModal();
        });
    });

    document.querySelectorAll("[data-modal-close]").forEach((button) => {
        button.addEventListener("click", () => button.closest("dialog")?.close());
    });
});
