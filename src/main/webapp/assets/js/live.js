/**
 * Solar Energy Monitoring System - Real-Time AJAX Telemetry Polling
 * Queries /api/telemetry every 5 seconds to update KPI cards, battery gauge, and charts.
 */

window.SolarLive = {
    timer: null,
    pollIntervalMs: 5000,
    contextPath: '',

    init(contextPath, ownerId = null, deviceId = null) {
        this.contextPath = contextPath || '';
        this.startPolling(ownerId, deviceId);
    },

    startPolling(ownerId, deviceId) {
        if (this.timer) clearInterval(this.timer);

        this.timer = setInterval(() => {
            let url = `${this.contextPath}/api/telemetry?t=${Date.now()}`;
            if (ownerId) url += `&ownerId=${ownerId}`;
            if (deviceId) url += `&deviceId=${deviceId}`;

            fetch(url)
                .then(res => {
                    if (!res.ok) throw new Error(`HTTP ${res.status}`);
                    return res.json();
                })
                .then(data => {
                    this.updateDom(data);
                })
                .catch(err => {
                    console.debug('Telemetry poll pause:', err.message);
                });
        }, this.pollIntervalMs);
    },

    updateDom(data) {
        if (!data || data.status !== 'success') return;

        const summary = data.summary || {};

        // 1. Update text metrics with class-based targets
        this.setText('liveProdKw', summary.currentProdKw != null ? summary.currentProdKw.toFixed(1) : null);
        this.setText('liveConsKw', summary.todayConsKwh != null ? (summary.todayConsKwh / 24).toFixed(1) : null);
        this.setText('liveTodayKwh', summary.todayProdKwh != null ? summary.todayProdKwh.toFixed(1) : null);
        this.setText('liveNetKw', summary.netPowerKw != null ? summary.netPowerKw.toFixed(1) : null);

        // 2. Update battery gauge if present
        if (data.battery) {
            const batt = data.battery;
            this.setText('liveBattPct', Math.round(batt.chargePercent) + '%');
            this.setText('liveBattState', batt.state);
            this.setText('liveTimeToEmpty', batt.timeToEmptyHours + ' hrs');

            if (window.SolarCharts && typeof window.SolarCharts.updateBatteryGauge === 'function') {
                window.SolarCharts.updateBatteryGauge(
                    'batteryGaugeCircle',
                    'batteryGaugeText',
                    'batteryGaugeState',
                    batt.chargePercent,
                    batt.state
                );
            }
        }

        // 3. Update Chart.js datasets if hourly telemetry returned
        if (data.hourly && Array.isArray(data.hourly) && data.hourly.length > 0) {
            this.updateCharts(data.hourly);
        }
    },

    setText(idOrClass, value) {
        if (value == null) return;
        const el = document.getElementById(idOrClass);
        if (el) el.textContent = value;

        document.querySelectorAll('.' + idOrClass).forEach(node => {
            node.textContent = value;
        });
    },

    updateCharts(hourly) {
        const labels = hourly.map(h => h.label);
        const prodPoints = hourly.map(h => h.productionKw);
        const consPoints = hourly.map(h => h.consumptionKw);

        // Update single production chart if present
        const prodChart = window.SolarCharts ? window.SolarCharts.instances['productionChart'] : null;
        if (prodChart) {
            prodChart.data.labels = labels;
            prodChart.data.datasets[0].data = prodPoints;
            prodChart.update('none'); // Silent update without disruptive re-animation
        }

        // Update dual comparison chart if present
        const compChart = window.SolarCharts ? window.SolarCharts.instances['comparisonChart'] : null;
        if (compChart) {
            compChart.data.labels = labels;
            compChart.data.datasets[0].data = prodPoints;
            compChart.data.datasets[1].data = consPoints;
            compChart.update('none');
        }
    }
};
