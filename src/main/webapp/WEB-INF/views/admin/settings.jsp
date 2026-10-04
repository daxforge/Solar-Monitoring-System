<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="System Settings &amp; Governance" scope="request"/>
<c:set var="activeNav" value="settings" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <div style="max-width: 820px; margin: 0 auto;">
                <div class="card" style="margin-bottom: 2rem;">
                    <div class="card-header">
                        <div class="card-title">
                            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="var(--solar-amber)" stroke-width="2">
                                <circle cx="12" cy="12" r="3"></circle>
                                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
                            </svg>
                            <span>Global Thresholds &amp; Economics</span>
                        </div>
                        <span class="badge" style="font-size:0.75rem; color:var(--text-muted);">Active Policy</span>
                    </div>

                    <form action="${pageContext.request.contextPath}/admin/settings" method="post">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">

                        <!-- General System Identity -->
                        <div style="margin-bottom: 1.5rem;">
                            <label for="systemName">System Platform Title</label>
                            <input type="text" id="systemName" name="systemName" value="<c:out value="${settings['system_name']}"/>" required>
                            <span style="font-size:0.75rem; color:var(--text-muted);">The primary branding title displayed across all homeowner portals and receipts.</span>
                        </div>

                        <!-- Tariff Settings -->
                        <div style="margin-bottom: 1.5rem;">
                            <label for="tariffPerKwh">Grid Feed-in / Consumption Tariff ($ per kWh)</label>
                            <input type="number" step="0.01" min="0.01" id="tariffPerKwh" name="tariffPerKwh" value="<c:out value="${settings['tariff_per_kwh']}"/>" required>
                            <span style="font-size:0.75rem; color:var(--text-muted);">Base unit price used for financial billing estimates on consumption reports.</span>
                        </div>

                        <!-- Safety Alert Thresholds -->
                        <div style="border-top: 1px solid var(--border-subtle); padding-top: 1.5rem; margin-bottom: 1.5rem;">
                            <h3 style="font-size: 1rem; margin-bottom: 1rem;">Automated Alert Engine Rule Triggers</h3>

                            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1.25rem; margin-bottom: 1.25rem;">
                                <div>
                                    <label for="lowBatteryThreshold">Low Battery Critical Cutoff (%)</label>
                                    <input type="number" step="1" min="5" max="50" id="lowBatteryThreshold" name="lowBatteryThreshold" value="<c:out value="${settings['low_battery_threshold']}"/>" required>
                                    <span style="font-size:0.75rem; color:var(--text-muted);">Triggers a critical safety alert if state-of-charge drops below this reserve level.</span>
                                </div>
                                <div>
                                    <label for="lowProductionThreshold">Solar Production Drop Anomaly (%)</label>
                                    <input type="number" step="1" min="10" max="90" id="lowProductionThreshold" name="lowProductionThreshold" value="<c:out value="${settings['low_production_threshold']}"/>" required>
                                    <span style="font-size:0.75rem; color:var(--text-muted);">Flags arrays underperforming expected daytime irradiance curves.</span>
                                </div>
                            </div>

                            <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1.25rem;">
                                <div>
                                    <label for="highConsumptionThreshold">High Consumption Load Spike (kW)</label>
                                    <input type="number" step="0.5" min="1" max="50" id="highConsumptionThreshold" name="highConsumptionThreshold" value="<c:out value="${settings['high_consumption_threshold']}"/>" required>
                                    <span style="font-size:0.75rem; color:var(--text-muted);">Alerts when a residence experiences heavy simultaneous appliance demand.</span>
                                </div>
                                <div>
                                    <label for="refreshIntervalSec">Telemetry Telecast Frequency (Seconds)</label>
                                    <input type="number" step="1" min="2" max="60" id="refreshIntervalSec" name="refreshIntervalSec" value="<c:out value="${settings['refresh_interval_sec']}"/>" required>
                                    <span style="font-size:0.75rem; color:var(--text-muted);">Cadence for background simulator cycles and live Chart.js polling.</span>
                                </div>
                            </div>
                        </div>

                        <div style="display:flex; justify-content:flex-end; gap:1rem; border-top:1px solid var(--border-subtle); padding-top:1.25rem;">
                            <button type="submit" class="btn btn-primary" style="padding: 0.7rem 2rem;">
                                Commit System Configuration
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
