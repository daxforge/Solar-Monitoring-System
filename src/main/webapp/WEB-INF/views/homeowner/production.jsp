<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Solar Energy Production" scope="request"/>
<c:set var="activeNav" value="production" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <!-- Hardware Selector Toolbar -->
            <div class="card" style="margin-bottom: 2rem; padding: 1.25rem 1.5rem;">
                <form action="${pageContext.request.contextPath}/homeowner/production" method="get" id="deviceSelectForm" style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:1rem;">
                    <div style="display:flex; align-items:center; gap:1rem;">
                        <label for="deviceSelect" style="margin:0; font-size:0.95rem; font-weight:700;">Select Photovoltaic Array:</label>
                        <select id="deviceSelect" name="deviceId" onchange="document.getElementById('deviceSelectForm').submit()" style="max-width:320px;">
                            <c:forEach var="d" items="${devices}">
                                <option value="${d.id}" ${selectedDevice != null && selectedDevice.id == d.id ? 'selected' : ''}>
                                    <c:out value="${d.name}"/> (<c:out value="${d.deviceCode}"/> - <c:out value="${d.capacityKw}"/> kW)
                                </option>
                            </c:forEach>
                        </select>
                    </div>

                    <div style="display:flex; align-items:center; gap:0.5rem;">
                        <span class="status-chip ${selectedDevice.status.cssClass}">
                            <span class="chip-dot"></span>
                            <c:out value="${selectedDevice.status.label}"/>
                        </span>
                        <span style="font-size:0.85rem; color:var(--text-muted);">
                            Location: <c:out value="${selectedDevice.location}"/>
                        </span>
                    </div>
                </form>
            </div>

            <!-- Real-time Production KPIs -->
            <div class="grid-3">
                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Instantaneous Output</span>
                        <div class="kpi-icon" style="background: var(--solar-amber-light); color: var(--solar-amber);">
                            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveProdKw" id="liveProdKw"><c:out value="${currentKw}"/></span>
                        <span class="kpi-unit">kW</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Current Sun Irradiance</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Generated Today</span>
                        <div class="kpi-icon" style="background: var(--solar-coral-light); color: var(--solar-coral);">
                            <svg viewBox="0 0 24 24"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"></path></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveTodayKwh" id="liveTodayKwh"><c:out value="${todayKwh}"/></span>
                        <span class="kpi-unit">kWh</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Accumulated Energy</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Rated Peak Capacity</span>
                        <div class="kpi-icon" style="background: var(--solar-sky-light); color: var(--solar-sky);">
                            <svg viewBox="0 0 24 24"><rect x="2" y="3" width="20" height="14" rx="2" ry="2"></rect><line x1="8" y1="21" x2="16" y2="21"></line></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number"><c:out value="${selectedDevice.capacityKw}"/></span>
                        <span class="kpi-unit">kWp</span>
                    </div>
                    <div><span class="kpi-trend trend-neutral">Hardware Rating</span></div>
                </div>
            </div>

            <!-- Live Line Chart -->
            <div class="card" style="margin-bottom: 2rem;">
                <div class="card-header">
                    <div class="card-title">
                        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-amber)" stroke-width="2">
                            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline>
                        </svg>
                        <span>Real-Time Solar Generation Curve (Today)</span>
                    </div>
                    <span class="badge" style="font-size:0.75rem; color:var(--text-muted);">Auto-refreshed via AJAX every 5s</span>
                </div>
                <div style="height: 350px; width: 100%;">
                    <canvas id="productionChart"></canvas>
                </div>
            </div>

            <!-- Recent Telemetry Reading Log -->
            <div class="card table-card">
                <div class="table-toolbar">
                    <div class="card-title">
                        <span>Recent Sensor Telemetry Readings</span>
                    </div>
                    <span style="font-size:0.85rem; color:var(--text-muted);">High-frequency data stream</span>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Timestamp</th>
                                <th>Solar Generation (kW)</th>
                                <th>Inverter Parasitic Load (kW)</th>
                                <th>Calculated Net Export (kW)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${recentReadings}">
                                <tr>
                                    <td><c:out value="${r.readingTime.toLocalTime().toString().substring(0,8)}"/></td>
                                    <td><strong style="color:var(--solar-amber);"><c:out value="${r.productionKw}"/> kW</strong></td>
                                    <td><c:out value="${r.consumptionKw}"/> kW</td>
                                    <td><span style="color:var(--solar-emerald); font-weight:600;"><c:out value="${r.netPowerKw}"/> kW</span></td>
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
        const rawJson = '${chartHourlyJson}';
        let hourlyData = [];
        try { hourlyData = JSON.parse(rawJson); } catch(e) {}

        const labels = hourlyData.map(h => h.label);
        const prodData = hourlyData.map(h => h.productionKw);

        window.SolarCharts.renderProductionChart('productionChart', labels, prodData);

        // Start live polling for selected device
        const deviceId = '${selectedDevice != null ? selectedDevice.id : ""}';
        window.SolarLive.init(window.APP_CONTEXT, '${sessionScope.currentUser.id}', deviceId);
    });
</script>
