<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Energy Consumption &amp; Costs" scope="request"/>
<c:set var="activeNav" value="consumption" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <!-- Period Filter Toolbar -->
            <div class="card" style="margin-bottom: 2rem; padding: 1.25rem 1.5rem;">
                <form action="${pageContext.request.contextPath}/homeowner/consumption" method="get" id="consumptionFilterForm" style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:1rem;">
                    <!-- Filter Pills -->
                    <div style="display:flex; align-items:center; gap:0.5rem; flex-wrap:wrap;">
                        <span style="font-weight:700; margin-right:0.5rem;">Analysis Period:</span>
                        <a href="${pageContext.request.contextPath}/homeowner/consumption?period=today"
                           class="btn btn-sm ${selectedPeriod == 'today' ? 'btn-primary' : 'btn-secondary'}">
                            Today
                        </a>
                        <a href="${pageContext.request.contextPath}/homeowner/consumption?period=7days"
                           class="btn btn-sm ${selectedPeriod == '7days' ? 'btn-primary' : 'btn-secondary'}">
                            Past 7 Days
                        </a>
                        <a href="${pageContext.request.contextPath}/homeowner/consumption?period=30days"
                           class="btn btn-sm ${selectedPeriod == '30days' ? 'btn-primary' : 'btn-secondary'}">
                            Past 30 Days
                        </a>
                    </div>

                    <!-- Custom Date Picker Range -->
                    <div style="display:flex; align-items:center; gap:0.5rem;">
                        <input type="hidden" name="period" value="custom">
                        <input type="date" name="from" value="<c:out value="${customFrom}"/>" style="width:150px; padding:0.4rem 0.6rem; font-size:0.85rem;" required>
                        <span style="color:var(--text-muted);">&rarr;</span>
                        <input type="date" name="to" value="<c:out value="${customTo}"/>" style="width:150px; padding:0.4rem 0.6rem; font-size:0.85rem;" required>
                        <button type="submit" class="btn btn-secondary btn-sm">Filter</button>
                    </div>
                </form>
            </div>

            <!-- Financial & Consumption KPIs -->
            <div class="grid-3">
                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Total Energy Drawn</span>
                        <div class="kpi-icon" style="background: var(--solar-sky-light); color: var(--solar-sky);">
                            <svg viewBox="0 0 24 24"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number count-up" data-target="${totalKwh}"><c:out value="${totalKwh}"/></span>
                        <span class="kpi-unit">kWh</span>
                    </div>
                    <div><span class="kpi-trend trend-neutral">Aggregated Load</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Estimated Utility Cost</span>
                        <div class="kpi-icon" style="background: var(--solar-coral-light); color: var(--solar-coral);">
                            <svg viewBox="0 0 24 24"><line x1="12" y1="1" x2="12" y2="23"></line><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number count-up" data-target="${totalCost}">$<c:out value="${totalCost}"/></span>
                        <span class="kpi-unit">USD</span>
                    </div>
                    <div><span class="kpi-trend trend-down">Tariff: $<c:out value="${tariff}"/>/kWh</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Active Billing Tariff</span>
                        <div class="kpi-icon" style="background: var(--solar-amber-light); color: var(--solar-amber);">
                            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"></circle><path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8"></path></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number">$<c:out value="${tariff}"/></span>
                        <span class="kpi-unit">/ kWh</span>
                    </div>
                    <div><span class="kpi-trend trend-neutral">Admin Configured Rate</span></div>
                </div>
            </div>

            <!-- Consumption Trend Chart -->
            <div class="card" style="margin-bottom: 2rem;">
                <div class="card-header">
                    <div class="card-title">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-sky)" stroke-width="2">
                            <line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line>
                        </svg>
                        <span>Consumption Profile &amp; Load Demand Trend</span>
                    </div>
                    <span class="badge" style="font-size:0.75rem; color:var(--text-muted);"><c:out value="${selectedPeriod}"/> overview</span>
                </div>
                <div style="height: 330px; width: 100%;">
                    <canvas id="consumptionChart"></canvas>
                </div>
            </div>

            <!-- Breakdown Table with Peak Hour and Cost -->
            <div class="card table-card">
                <div class="table-toolbar">
                    <div>
                        <h3 style="font-size: 1.1rem;">Periodical Consumption Breakdown</h3>
                        <p style="font-size: 0.82rem;">Granular tracking of daily consumption totals, peak load hours, and calculated costs.</p>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Date</th>
                                <th>Total Energy (kWh)</th>
                                <th>Peak Load Window &amp; Demand</th>
                                <th>Average Baseline Load (kW)</th>
                                <th>Tariff Rate</th>
                                <th>Estimated Cost</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="row" items="${reportRows}">
                                <tr>
                                    <td><strong><c:out value="${row.date}"/></strong></td>
                                    <td><c:out value="${row.totalKwh}"/> kWh</td>
                                    <td>
                                        <span class="status-chip chip-maintenance">
                                            <c:out value="${row.peakHour}"/>
                                        </span>
                                    </td>
                                    <td><c:out value="${row.avgLoadKw}"/> kW</td>
                                    <td>$<c:out value="${row.tariff}"/></td>
                                    <td><strong style="color:var(--solar-coral);">$<c:out value="${row.estimatedCost}"/></strong></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const rawJson = '${chartConsumptionJson}';
        let chartData = [];
        try { chartData = JSON.parse(rawJson); } catch(e) {}

        const labels = chartData.map(c => c.label);
        const consPoints = chartData.map(c => c.consumptionKwh != null ? c.consumptionKwh : c.consumptionKw);

        const canvas = document.getElementById('consumptionChart');
        if (canvas) {
            const ctx = canvas.getContext('2d');
            const grad = ctx.createLinearGradient(0, 0, 0, 300);
            grad.addColorStop(0, 'rgba(2, 132, 199, 0.4)');
            grad.addColorStop(1, 'rgba(2, 132, 199, 0.0)');

            new Chart(ctx, {
                type: 'line',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Consumption (${selectedPeriod == "today" ? "kW" : "kWh"})',
                        data: consPoints,
                        borderColor: '#0284c7',
                        borderWidth: 3,
                        backgroundColor: grad,
                        fill: true,
                        tension: 0.35,
                        pointBackgroundColor: '#ffffff',
                        pointBorderColor: '#0284c7',
                        pointRadius: 4
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    scales: {
                        x: { grid: { display: false }, ticks: { color: SolarCharts.getTextColor() } },
                        y: { grid: { color: SolarCharts.getGridColor() }, ticks: { color: SolarCharts.getTextColor() }, beginAtZero: true }
                    }
                }
            });
        }
    });
</script>
