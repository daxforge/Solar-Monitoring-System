<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="System Alerts &amp; Notifications" scope="request"/>
<c:set var="activeNav" value="alerts" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <div class="card table-card">
                <div class="table-toolbar">
                    <div>
                        <div style="display:flex; align-items:center; gap:0.75rem;">
                            <h2 style="font-size: 1.25rem;">Safety &amp; Operational Alert Stream</h2>
                            <c:if test="${unreadCount > 0}">
                                <span class="severity-badge severity-critical"><c:out value="${unreadCount}"/> New</span>
                            </c:if>
                        </div>
                        <p style="font-size: 0.85rem;">Autonomous threshold checks triggered by high consumption spikes, low battery, and offline devices.</p>
                    </div>

                    <div style="display:flex; align-items:center; gap:0.75rem;">
                        <a href="${pageContext.request.contextPath}/alerts"
                           class="btn btn-sm ${currentFilter == 'all' ? 'btn-primary' : 'btn-secondary'}">
                            All Notifications
                        </a>
                        <a href="${pageContext.request.contextPath}/alerts?filter=unread"
                           class="btn btn-sm ${currentFilter == 'unread' ? 'btn-primary' : 'btn-secondary'}">
                            Unread Only
                        </a>

                        <c:if test="${unreadCount > 0}">
                            <form action="${pageContext.request.contextPath}/alerts" method="post" style="display:inline;">
                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                <input type="hidden" name="action" value="markAllRead">
                                <button type="submit" class="btn btn-secondary btn-sm">Mark All as Read</button>
                            </form>
                        </c:if>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="custom-table">
                        <thead>
                            <tr>
                                <th>Severity</th>
                                <th>Event Description</th>
                                <th>Hardware Source</th>
                                <th>Timestamp</th>
                                <th>Read Status</th>
                                <th style="text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty alerts}">
                                    <c:forEach var="a" items="${alerts}">
                                        <tr style="${!a.read ? 'background-color: var(--bg-surface-subtle);' : ''}">
                                            <td>
                                                <span class="severity-badge ${a.severity.badgeClass}">
                                                    <c:out value="${a.severity.label}"/>
                                                </span>
                                            </td>
                                            <td>
                                                <strong><c:out value="${a.message}"/></strong>
                                            </td>
                                            <td>
                                                <c:out value="${a.deviceName != null ? a.deviceName : 'System Engine'}"/>
                                                <c:if test="${a.deviceCode != null}">
                                                    (<code style="font-size:0.8rem;"><c:out value="${a.deviceCode}"/></code>)
                                                </c:if>
                                            </td>
                                            <td><c:out value="${a.createdAt.toLocalDate()}"/> <c:out value="${a.createdAt.toLocalTime().toString().substring(0,5)}"/></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${a.read}">
                                                        <span style="color:var(--text-muted); font-size:0.8rem;">Acknowledged</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-chip chip-offline" style="padding:0.15rem 0.5rem; font-size:0.7rem;">Unread</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td style="text-align: right;">
                                                <c:if test="${!a.read}">
                                                    <form action="${pageContext.request.contextPath}/alerts" method="post" style="display:inline;">
                                                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                        <input type="hidden" name="action" value="markRead">
                                                        <input type="hidden" name="alertId" value="${a.id}">
                                                        <button type="submit" class="btn btn-secondary btn-sm">Mark Read</button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="6" style="text-align:center; padding:3rem; color:var(--text-muted);">
                                            No system alerts registered under this filter. All telemetric safety parameters are nominal.
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
