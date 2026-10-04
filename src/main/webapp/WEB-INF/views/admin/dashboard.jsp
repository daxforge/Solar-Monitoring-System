<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Admin Command Center" scope="request"/>
<c:set var="activeNav" value="adminDashboard" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <!-- Executive KPI Cards -->
            <div class="grid-4">
                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Active Users</span>
                        <div class="kpi-icon" style="background: var(--solar-sky-light); color: var(--solar-sky);">
                            <svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number count-up" data-target="${activeUsers}"><c:out value="${activeUsers}"/></span>
                        <span class="kpi-unit">accounts</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Managed Accounts</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Active Hardware</span>
                        <div class="kpi-icon" style="background: var(--solar-emerald-light); color: var(--solar-emerald);">
                            <svg viewBox="0 0 24 24"><rect x="2" y="2" width="20" height="8" rx="2"></rect><rect x="2" y="14" width="20" height="8" rx="2"></rect></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number count-up" data-target="${activeDevices}"><c:out value="${activeDevices}"/></span>
                        <span class="kpi-unit">/ ${totalDevices} online</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Telemetry Active</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Current Grid Generation</span>
                        <div class="kpi-icon" style="background: var(--solar-amber-light); color: var(--solar-amber);">
                            <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveProdKw"><c:out value="${systemSummary.currentProdKw}"/></span>
                        <span class="kpi-unit">kW</span>
                    </div>
                    <div><span class="kpi-trend trend-neutral">Aggregated Real-time</span></div>
                </div>

                <div class="card kpi-card">
                    <div class="kpi-top">
                        <span class="kpi-label">Total Generation Today</span>
                        <div class="kpi-icon" style="background: var(--solar-coral-light); color: var(--solar-coral);">
                            <svg viewBox="0 0 24 24"><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"></path></svg>
                        </div>
                    </div>
                    <div class="kpi-value-row">
                        <span class="kpi-number liveTodayKwh"><c:out value="${systemSummary.todayProdKwh}"/></span>
                        <span class="kpi-unit">kWh</span>
                    </div>
                    <div><span class="kpi-trend trend-up">Clean Harvest</span></div>
                </div>
            </div>

            <!-- Charts Section: Overall Grid Generation vs Consumption Load -->
            <div class="grid-2-1">
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-amber)" stroke-width="2">
                                <line x1="18" y1="20" x2="18" y2="10"></line><line x1="12" y1="20" x2="12" y2="4"></line><line x1="6" y1="20" x2="6" y2="14"></line>
                            </svg>
                            <span>Overall System Production &amp; Household Load Today</span>
                        </div>
                        <span class="badge" style="font-size:0.75rem; color:var(--text-muted);">Synchronized 5s interval</span>
                    </div>
                    <div style="height: 320px; width: 100%;">
                        <canvas id="comparisonChart"></canvas>
                    </div>
                </div>

                <!-- Recent Alerts Stream -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-coral)" stroke-width="2">
                                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
                            </svg>
                            <span>Recent Alerts</span>
                        </div>
                        <a href="${pageContext.request.contextPath}/alerts" class="btn btn-secondary btn-sm">View All</a>
                    </div>
                    <div style="display:flex; flex-direction:column; gap:0.75rem; max-height:310px; overflow-y:auto;">
                        <c:forEach var="alert" items="${recentAlerts}">
                            <div style="padding:0.75rem; border-radius:var(--radius-sm); background:var(--bg-surface-subtle); display:flex; align-items:flex-start; gap:0.75rem;">
                                <span class="severity-badge ${alert.severity.badgeClass}"><c:out value="${alert.severity.label}"/></span>
                                <div style="flex:1; min-width:0;">
                                    <div style="font-size:0.85rem; font-weight:600; color:var(--text-main);"><c:out value="${alert.message}"/></div>
                                    <div style="font-size:0.75rem; color:var(--text-muted);"><c:out value="${alert.userName}"/> &bull; <c:out value="${alert.deviceCode}"/></div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>

            <!-- Hardware Fleet Status Table -->
            <div class="card table-card">
                <div class="table-toolbar">
                    <div class="card-title">
                        <span>Hardware Infrastructure Snapshot</span>
                    </div>
                    <div class="search-input-wrap">
                        <svg class="search-icon" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                        <input type="text" class="table-search-input" data-table-target="fleetTable" placeholder="Filter devices or homeowners...">
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/devices" class="btn btn-secondary btn-sm">Manage All Devices</a>
                </div>

                <div class="table-responsive">
                    <table class="custom-table" id="fleetTable">
                        <thead>
                            <tr>
                                <th>Device Code</th>
                                <th>Hardware Name</th>
                                <th>Category</th>
                                <th>Assigned Homeowner</th>
                                <th>Capacity</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${devices}">
                                <tr>
                                    <td style="font-family: monospace; font-weight:600;"><c:out value="${d.deviceCode}"/></td>
                                    <td><strong><c:out value="${d.name}"/></strong></td>
                                    <td><c:out value="${d.type.displayName}"/></td>
                                    <td><c:out value="${d.ownerName != null ? d.ownerName : 'Unassigned'}"/></td>
                                    <td><c:out value="${d.capacityKw}"/> kW</td>
                                    <td>
                                        <span class="status-chip ${d.status.cssClass}">
                                            <span class="chip-dot"></span>
                                            <c:out value="${d.status.label}"/>
                                        </span>
                                    </td>
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
        // Initial chart setup from server JSON
        const rawJson = '${chartHourlyJson}';
        let hourlyData = [];
        try { hourlyData = JSON.parse(rawJson); } catch(e) {}

        const labels = hourlyData.map(h => h.label);
        const prodData = hourlyData.map(h => h.productionKw);
        const consData = hourlyData.map(h => h.consumptionKw);

        window.SolarCharts.renderComparisonChart('comparisonChart', labels, prodData, consData);

        // Start live 5s telemetry polling
        window.SolarLive.init(window.APP_CONTEXT, null, null);
    });
</script>
