<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Solar Energy Console" scope="request"/>
<c:set var="activeNav" value="homeDashboard" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <!-- Homeowner Real-time KPI Metric Cards -->
            <div class="grid-4">
                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Current Solar Harvest</span>
                        <div class="kpi-icon" style="background: var(--solar-amber-light); color: var(--solar-amber);">
                            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveProdKw"><c:out value="${summary.currentProdKw}"/></span>
                        <span class="kpi-unit">kW</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Streaming Live</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Harvested Today</span>
                        <div class="kpi-icon" style="background: var(--solar-coral-light); color: var(--solar-coral);">
                            <svg viewBox="0 0 24 24"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"></path></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveTodayKwh"><c:out value="${summary.todayProdKwh}"/></span>
                        <span class="kpi-unit">kWh</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Photovoltaic Yield</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Household Consumption</span>
                        <div class="kpi-icon" style="background: var(--solar-sky-light); color: var(--solar-sky);">
                            <svg viewBox="0 0 24 24"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number"><c:out value="${summary.todayConsKwh}"/></span>
                        <span class="kpi-unit">kWh</span>
                    </div>
                    <div><span class="kpi-trend trend-neutral">Daily Load</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Energy Storage Reserve</span>
                        <div class="kpi-icon" style="background: var(--solar-emerald-light); color: var(--solar-emerald);">
                            <svg viewBox="0 0 24 24"><rect x="1" y="6" width="18" height="12" rx="2"></rect><line x1="23" y1="11" x2="23" y2="13"></line></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveBattPct"><c:out value="${summary.batteryPercent}"/>%</span>
                        <span class="kpi-unit">SoC</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Battery Status</span></div>
                </div>
            </div>

            <!-- Charts & Storage Gauge Section -->
            <div class="grid-2-1">
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-amber)" stroke-width="2">
                                <circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line>
                            </svg>
                            <span>Solar Generation vs. Consumption Load Today</span>
                        </div>
                        <span class="badge" style="font-size:0.75rem; color:var(--text-muted);">Real-time Telemetry</span>
                    </div>
                    <div style="height: 330px; width: 100%;">
                        <canvas id="comparisonChart"></canvas>
                    </div>
                </div>

                <!-- Circular Battery Gauge Card -->
                <div class="card battery-gauge-wrapper">
                    <div class="card-header" style="width:100%;">
                        <div class="card-title">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-emerald)" stroke-width="2">
                                <rect x="1" y="6" width="18" height="12" rx="2"></rect><line x1="23" y1="11" x2="23" y2="13"></line>
                            </svg>
                            <span>Battery Vault</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/homeowner/storage" class="btn btn-secondary btn-sm">Details</a>
                    </div>

                    <div class="gauge-svg-container" style="margin: 1rem 0;">
                        <svg class="gauge-svg" viewBox="0 0 160 160">
                            <defs>
                                <linearGradient id="batteryGradient" x1="0%" y1="0%" x2="100%" y2="100%">
                                    <stop offset="0%" stop-color="#10b981" />
                                    <stop offset="100%" stop-color="#0284c7" />
                                </linearGradient>
                            </defs>
                            <circle class="gauge-bg-circle" cx="80" cy="80" r="65"></circle>
                            <circle class="gauge-progress-circle" id="batteryGaugeCircle" cx="80" cy="80" r="65"></circle>
                        </svg>
                        <div class="gauge-content">
                            <div class="gauge-percentage" id="batteryGaugeText">
                                <c:out value="${primaryBattery != null ? primaryBattery.chargePercent : summary.batteryPercent}"/>%
                            </div>
                            <div class="gauge-state-badge" id="batteryGaugeState" style="background:var(--solar-emerald-light); color:var(--solar-emerald);">
                                <c:out value="${primaryBattery != null ? primaryBattery.state : 'CHARGING'}"/>
                            </div>
                        </div>
                    </div>

                    <div style="font-size:0.85rem; color:var(--text-secondary); width:100%; display:flex; justify-content:space-between; border-top:1px solid var(--border-subtle); padding-top:0.75rem;">
                        <span>Estimated Autonomy:</span>
                        <strong id="liveTimeToEmpty">
                            <c:out value="${primaryBattery != null ? primaryBattery.estimatedTimeToEmptyHours : 7.2}"/> hrs
                        </strong>
                    </div>
                </div>
            </div>

            <!-- Homeowner Hardware & Alerts -->
            <div class="grid-2">
                <!-- Hardware Units -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <span>Your Energy Hardware Fleet</span>
                        </div>
                        <span class="badge" style="font-size:0.75rem; color:var(--text-muted);"><c:out value="${devices.size()}"/> Devices</span>
                    </div>

                    <div style="display:flex; flex-direction:column; gap:0.75rem;">
                        <c:forEach var="d" items="${devices}">
                            <div style="padding:0.75rem 1rem; border-radius:var(--radius-md); background:var(--bg-surface-subtle); display:flex; align-items:center; justify-content:space-between;">
                                <div>
                                    <div style="font-weight:600; font-size:0.95rem; color:var(--text-main);"><c:out value="${d.name}"/></div>
                                    <div style="font-size:0.8rem; color:var(--text-muted);"><c:out value="${d.type.displayName}"/> &bull; <c:out value="${d.location}"/></div>
                                </div>
                                <div style="display:flex; align-items:center; gap:0.75rem;">
                                    <span style="font-weight:700; font-size:0.9rem;"><c:out value="${d.capacityKw}"/> kW</span>
                                    <span class="status-chip ${d.status.cssClass}"><c:out value="${d.status.label}"/></span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <!-- Recent Alerts -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <span>Safety &amp; Production Alerts</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/alerts" class="btn btn-secondary btn-sm">All Notifications</a>
                    </div>

                    <div style="display:flex; flex-direction:column; gap:0.75rem;">
                        <c:choose>
                            <c:when test="${not empty recentAlerts}">
                                <c:forEach var="a" items="${recentAlerts}">
                                    <div style="padding:0.75rem 1rem; border-radius:var(--radius-md); background:var(--bg-surface-subtle); display:flex; align-items:center; justify-content:space-between; gap:1rem;">
                                        <div style="display:flex; align-items:flex-start; gap:0.75rem;">
                                            <span class="severity-badge ${a.severity.badgeClass}"><c:out value="${a.severity.label}"/></span>
                                            <div>
                                                <div style="font-size:0.88rem; font-weight:600;"><c:out value="${a.message}"/></div>
                                                <div style="font-size:0.75rem; color:var(--text-muted);"><c:out value="${a.deviceName}"/> &bull; <c:out value="${a.createdAt.toLocalDate()}"/></div>
                                            </div>
                                        </div>
                                        <c:if test="${!a.read}">
                                            <span class="chip-dot" style="background:var(--solar-rose); width:8px; height:8px;" title="Unread"></span>
                                        </c:if>
                                    </div>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <div style="text-align:center; padding:2rem; color:var(--text-muted);">
                                    All energy systems operating within optimal thresholds.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        // Initialize dual production vs consumption chart
        const rawJson = '${chartHourlyJson}';
        let hourlyData = [];
        try { hourlyData = JSON.parse(rawJson); } catch(e) {}

        const labels = hourlyData.map(h => h.label);
        const prodData = hourlyData.map(h => h.productionKw);
        const consData = hourlyData.map(h => h.consumptionKw);

        window.SolarCharts.renderComparisonChart('comparisonChart', labels, prodData, consData);

        // Update Circular Battery Gauge
        const battPct = parseFloat('${primaryBattery != null ? primaryBattery.chargePercent : summary.batteryPercent}') || 78;
        const battState = '${primaryBattery != null ? primaryBattery.state : "CHARGING"}';
        window.SolarCharts.updateBatteryGauge('batteryGaugeCircle', 'batteryGaugeText', 'batteryGaugeState', battPct, battState);

        // Start live 5-second polling for this homeowner
        window.SolarLive.init(window.APP_CONTEXT, '${sessionScope.currentUser.id}', null);
    });
</script>
