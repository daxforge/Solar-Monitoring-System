/**
 * Solar Energy Monitoring System - Chart.js Integration
 * Elegant line and area charts with smooth cubic splines, radiant fills,
 * custom tooltips, and theme-adaptive colors.
 */

window.SolarCharts = {
    instances: {},

    getGridColor() {
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        return isDark ? 'rgba(255, 255, 255, 0.08)' : 'rgba(15, 23, 42, 0.06)';
    },

    getTextColor() {
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        return isDark ? '#94a3b8' : '#64748b';
    },

    /**
     * Renders a live production area chart with solar amber gradient fill.
     */
    renderProductionChart(canvasId, labels, dataPoints) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) return null;
        const ctx = canvas.getContext('2d');

        if (this.instances[canvasId]) {
            this.instances[canvasId].destroy();
        }

        const gradient = ctx.createLinearGradient(0, 0, 0, 300);
        gradient.addColorStop(0, 'rgba(245, 158, 11, 0.45)');
        gradient.addColorStop(0.7, 'rgba(251, 113, 133, 0.12)');
        gradient.addColorStop(1, 'rgba(245, 158, 11, 0.0)');

        const chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Solar Output (kW)',
                    data: dataPoints,
                    borderColor: '#f59e0b',
                    borderWidth: 3,
                    backgroundColor: gradient,
                    fill: true,
                    tension: 0.4,
                    pointBackgroundColor: '#ffffff',
                    pointBorderColor: '#f59e0b',
                    pointBorderWidth: 2,
                    pointRadius: 4,
                    pointHoverRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false },
                    tooltip: {
                        backgroundColor: 'rgba(15, 23, 42, 0.9)',
                        titleFont: { family: "'Space Grotesk', sans-serif", weight: 'bold' },
                        bodyFont: { family: "'Inter', sans-serif" },
                        padding: 10,
                        cornerRadius: 8,
                        callbacks: {
                            label: (context) => `Production: ${context.parsed.y} kW`
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { color: this.getTextColor(), font: { family: "'Inter', sans-serif", size: 11 } }
                    },
                    y: {
                        grid: { color: this.getGridColor() },
                        ticks: { color: this.getTextColor(), font: { family: "'Inter', sans-serif", size: 11 } },
                        beginAtZero: true
                    }
                }
            }
        });

        this.instances[canvasId] = chart;
        return chart;
    },

    /**
     * Renders a dual comparison chart: Production vs. Consumption.
     */
    renderComparisonChart(canvasId, labels, prodData, consData) {
        const canvas = document.getElementById(canvasId);
        if (!canvas) return null;
        const ctx = canvas.getContext('2d');

        if (this.instances[canvasId]) {
            this.instances[canvasId].destroy();
        }

        const prodGrad = ctx.createLinearGradient(0, 0, 0, 320);
        prodGrad.addColorStop(0, 'rgba(245, 158, 11, 0.35)');
        prodGrad.addColorStop(1, 'rgba(245, 158, 11, 0.0)');

        const consGrad = ctx.createLinearGradient(0, 0, 0, 320);
        consGrad.addColorStop(0, 'rgba(2, 132, 199, 0.25)');
        consGrad.addColorStop(1, 'rgba(2, 132, 199, 0.0)');

        const chart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [
                    {
                        label: 'Solar Production (kW)',
                        data: prodData,
                        borderColor: '#f59e0b',
                        borderWidth: 2.5,
                        backgroundColor: prodGrad,
                        fill: true,
                        tension: 0.4,
                        pointRadius: 3
                    },
                    {
                        label: 'Home Load (kW)',
                        data: consData,
                        borderColor: '#0284c7',
                        borderWidth: 2.5,
                        backgroundColor: consGrad,
                        fill: true,
                        tension: 0.4,
                        pointRadius: 3
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: {
                        position: 'top',
                        labels: {
                            color: this.getTextColor(),
                            font: { family: "'Space Grotesk', sans-serif", weight: '600', size: 12 },
                            usePointStyle: true
                        }
                    }
                },
                scales: {
                    x: {
                        grid: { display: false },
                        ticks: { color: this.getTextColor() }
                    },
                    y: {
                        grid: { color: this.getGridColor() },
                        ticks: { color: this.getTextColor() },
                        beginAtZero: true
                    }
                }
            }
        });

        this.instances[canvasId] = chart;
        return chart;
    },

    /**
     * Updates the circular battery SVG gauge stroke offset and text.
     */
    updateBatteryGauge(circleId, textId, stateId, percent, state) {
        const circle = document.getElementById(circleId);
        const text = document.getElementById(textId);
        const stateEl = document.getElementById(stateId);

        if (text) text.textContent = Math.round(percent) + '%';
        if (stateEl && state) stateEl.textContent = state;

        if (circle) {
            const radius = circle.r.baseVal.value;
            const circumference = 2 * Math.PI * radius;
            const offset = circumference - (percent / 100) * circumference;
            circle.style.strokeDasharray = `${circumference} ${circumference}`;
            circle.style.strokeDashoffset = offset;
        }
    }
};

// Re-render chart colors on dark/light theme switch
window.addEventListener('solarThemeChanged', () => {
    Object.values(SolarCharts.instances).forEach(chart => {
        if (chart && chart.options && chart.options.scales) {
            if (chart.options.scales.x && chart.options.scales.x.ticks) {
                chart.options.scales.x.ticks.color = SolarCharts.getTextColor();
            }
            if (chart.options.scales.y) {
                if (chart.options.scales.y.ticks) chart.options.scales.y.ticks.color = SolarCharts.getTextColor();
                if (chart.options.scales.y.grid) chart.options.scales.y.grid.color = SolarCharts.getGridColor();
            }
            chart.update();
        }
    });
});
