<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Battery Storage Management" scope="request"/>
<c:set var="activeNav" value="storage" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <!-- Battery Selector Toolbar -->
            <div class="card" style="margin-bottom: 2rem; padding: 1.25rem 1.5rem;">
                <form action="${pageContext.request.contextPath}/homeowner/storage" method="get" id="batterySelectForm" style="display:flex; align-items:center; justify-content:space-between; flex-wrap:wrap; gap:1rem;">
                    <div style="display:flex; align-items:center; gap:1rem;">
                        <label for="batterySelect" style="margin:0; font-size:0.95rem; font-weight:700;">Select Energy Storage Unit:</label>
                        <select id="batterySelect" name="deviceId" onchange="document.getElementById('batterySelectForm').submit()" style="max-width:320px;">
                            <c:forEach var="b" items="${batteryDevices}">
                                <option value="${b.id}" ${selectedDevice != null && selectedDevice.id == b.id ? 'selected' : ''}>
                                    <c:out value="${b.name}"/> (<c:out value="${b.deviceCode}"/> - <c:out value="${b.capacityKw}"/> kW)
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

            <!-- Battery Diagnostics Overview Grid -->
            <div class="grid-2-1">
                <!-- Large Interactive Circular SVG Battery Gauge -->
                <div class="card battery-gauge-wrapper" style="padding: 2.5rem 1.5rem;">
                    <h3 style="margin-bottom: 1.5rem;">State-of-Charge (SoC) Telemetry</h3>

                    <div class="gauge-svg-container" style="width:240px; height:240px;">
                        <svg class="gauge-svg" viewBox="0 0 160 160">
                            <defs>
                                <linearGradient id="batteryStorageGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                                    <stop offset="0%" stop-color="#10b981" />
                                    <stop offset="100%" stop-color="#0ea5e9" />
                                </linearGradient>
                            </defs>
                            <circle class="gauge-bg-circle" cx="80" cy="80" r="65"></circle>
                            <circle class="gauge-progress-circle" id="batteryMainCircle" cx="80" cy="80" r="65" style="stroke: url(#batteryStorageGrad); stroke-width: 14;"></circle>
                        </svg>

                        <div class="gauge-content">
                            <div class="gauge-percentage" id="batteryMainText" style="font-size: 3rem;">
                                <c:out value="${storageStatus != null ? storageStatus.chargePercent : 78.5}"/>%
                            </div>
                            <div class="gauge-state-badge" id="batteryMainState" style="background:var(--solar-emerald-light); color:var(--solar-emerald); font-size:0.85rem; padding:0.3rem 0.8rem;">
                                <c:out value="${storageStatus != null ? storageStatus.state : 'CHARGING'}"/>
                            </div>
                        </div>
                    </div>

                    <div style="margin-top: 1.5rem; text-align:center;">
                        <span style="font-size: 0.85rem; color: var(--text-muted);">Last diagnostic ping: <c:out value="${storageStatus != null ? storageStatus.updatedAt.toLocalTime().toString().substring(0,8) : 'Real-time'}"/></span>
                    </div>
                </div>

                <!-- Storage Key Parameters -->
                <div style="display:flex; flex-direction:column; gap:1.5rem;">
                    <div class="card kpi-card">
                        <div class="kpi-top">
                            <span class="kpi-label">Usable Storage Capacity</span>
                            <div class="kpi-icon" style="background: var(--solar-sky-light); color: var(--solar-sky);">
                                <svg viewBox="0 0 24 24"><rect x="1" y="6" width="18" height="12" rx="2"></rect><line x1="23" y1="11" x2="23" y2="13"></line></svg>
                            </div>
                        </div>
                        <div class="kpi-value-row">
                            <span class="kpi-number"><c:out value="${selectedDevice.capacityKw * 2.5}"/></span>
                            <span class="kpi-unit">kWh</span>
                        </div>
                        <div><span class="kpi-trend trend-up">Rated Chemical Cell</span></div>
                    </div>

                    <div class="card kpi-card">
                        <div class="kpi-top">
                            <span class="kpi-label">Cell State of Health (SoH)</span>
                            <div class="kpi-icon" style="background: var(--solar-emerald-light); color: var(--solar-emerald);">
                                <svg viewBox="0 0 24 24"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path><polyline points="22 4 12 14.01 9 11.01"></polyline></svg>
                            </div>
                        </div>
                        <div class="kpi-value-row">
                            <span class="kpi-number"><c:out value="${storageStatus != null ? storageStatus.healthPercent : 98.0}"/>%</span>
                            <span class="kpi-unit">health</span>
                        </div>
                        <div><span class="kpi-trend trend-up">Optimal Float Condition</span></div>
                    </div>

                    <div class="card kpi-card">
                        <div class="kpi-top">
                            <span class="kpi-label">Estimated Autonomy Runtime</span>
                            <div class="kpi-icon" style="background: var(--solar-amber-light); color: var(--solar-amber);">
                                <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                            </div>
                        </div>
                        <div class="kpi-value-row">
                            <span class="kpi-number">
                                <c:out value="${storageStatus != null ? storageStatus.estimatedTimeToEmptyHours : 7.2}"/>
                            </span>
                            <span class="kpi-unit">hours</span>
                        </div>
                        <div><span class="kpi-trend trend-neutral">Based on 1.5 kW continuous draw</span></div>
                    </div>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script>
    document.addEventListener('DOMContentLoaded', () => {
        const pct = parseFloat('${storageStatus != null ? storageStatus.chargePercent : 78.5}') || 78;
        const state = '${storageStatus != null ? storageStatus.state : "CHARGING"}';
        window.SolarCharts.updateBatteryGauge('batteryMainCircle', 'batteryMainText', 'batteryMainState', pct, state);

        // Start live polling
        const deviceId = '${selectedDevice != null ? selectedDevice.id : ""}';
        window.SolarLive.init(window.APP_CONTEXT, '${sessionScope.currentUser.id}', deviceId);
    });
</script>
