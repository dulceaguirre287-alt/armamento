document.addEventListener("DOMContentLoaded", () => {
    const data = window.armamentoDashboard;
    if (!data || !window.Chart) {
        return;
    }

    const colors = {
        primary: "#0B5D4B",
        navy: "#0F2A43",
        amber: "#F59E0B",
        blue: "#0EA5E9",
        green: "#16A34A"
    };

    const draw = (id, config) => {
        const canvas = document.getElementById(id);
        if (canvas) new Chart(canvas, config);
    };

    draw("ventasChart", {
        type: "line",
        data: {
            labels: data.months,
            datasets: [{
                label: "Ventas (COP)",
                data: data.sales,
                borderColor: colors.primary,
                backgroundColor: "rgb(11 93 75 / 14%)",
                fill: true,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { y: { beginAtZero: true } }
        }
    });

    draw("productosChart", {
        type: "bar",
        data: {
            labels: data.products,
            datasets: [{
                label: "Unidades",
                data: data.productQuantities,
                backgroundColor: colors.amber,
                borderRadius: 6
            }]
        },
        options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } }
    });

    draw("almacenesChart", {
        type: "doughnut",
        data: {
            labels: data.warehouses,
            datasets: [{
                data: data.warehouseStock,
                backgroundColor: [colors.primary, colors.navy, colors.amber, colors.blue, colors.green]
            }]
        },
        options: { responsive: true, maintainAspectRatio: false }
    });
});
