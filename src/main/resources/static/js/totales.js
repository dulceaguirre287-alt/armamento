document.addEventListener("DOMContentLoaded", () => {
    const currency = new Intl.NumberFormat("es-CO", {
        style: "currency",
        currency: "COP",
        maximumFractionDigits: 2
    });

    const updateLine = (line, productChanged = false) => {
        const product = line.querySelector("[data-line-product]");
        const option = product?.selectedOptions[0];
        const price = line.querySelector("[data-line-price]");
        const tax = line.querySelector("[data-line-tax]");
        if (option && price && (product.dataset.lineProduct !== "purchase" || productChanged || !Number(price.value))) {
            price.value = option.dataset.price || "0";
        }
        if (tax) tax.value = option?.dataset.tax || "0";
    };

    const updateTotals = (form) => {
        let subtotal = 0;
        let taxTotal = 0;
        form.querySelectorAll("[data-line-item]").forEach((line) => {
            const quantity = Number(line.querySelector("[data-line-quantity]")?.value || 0);
            const price = Number(line.querySelector("[data-line-price]")?.value || 0);
            const rate = Number(line.querySelector("[data-line-tax]")?.value || 0);
            const lineSubtotal = quantity * price;
            subtotal += lineSubtotal;
            taxTotal += lineSubtotal * rate / 100;
        });
        const subtotalTarget = form.querySelector("[data-total-subtotal]");
        const taxTarget = form.querySelector("[data-total-tax]");
        const totalTarget = form.querySelector("[data-total-grand]");
        if (subtotalTarget) subtotalTarget.textContent = currency.format(subtotal);
        if (taxTarget) taxTarget.textContent = currency.format(taxTotal);
        if (totalTarget) totalTarget.textContent = currency.format(subtotal + taxTotal);
    };

    document.querySelectorAll("[data-totals-form]").forEach((form) => {
        const refresh = (event) => {
            const line = event.target.closest("[data-line-item]");
            if (line && event.target.matches("[data-line-product]")) {
                updateLine(line, true);
            }
            updateTotals(form);
        };
        form.addEventListener("input", refresh);
        form.addEventListener("change", refresh);

        form.querySelector("[data-add-line]")?.addEventListener("click", () => {
            const template = form.querySelector("[data-line-template]");
            const target = form.querySelector("[data-lines]");
            if (!template || !target) return;
            const row = template.content.firstElementChild.cloneNode(true);
            const index = target.querySelectorAll("[data-line-item]").length;
            row.querySelectorAll("[data-name]").forEach((field) => {
                field.name = field.dataset.name.replaceAll("__index__", String(index));
                field.removeAttribute("data-name");
            });
            target.append(row);
            updateTotals(form);
        });

        const reindexRows = () => {
            form.querySelectorAll("[data-line-item]").forEach((line, index) => {
                line.querySelectorAll("[name]").forEach((field) => {
                    field.name = field.name.replace(/detalles\[\d+]/, `detalles[${index}]`);
                });
            });
        };

        form.addEventListener("click", (event) => {
            const remove = event.target.closest("[data-remove-line]");
            if (!remove) return;
            const rows = form.querySelectorAll("[data-line-item]");
            if (rows.length <= 1) return;
            remove.closest("[data-line-item]")?.remove();
            reindexRows();
            updateTotals(form);
        });

        form.querySelectorAll("[data-line-item]").forEach(updateLine);
        updateTotals(form);
    });
});
