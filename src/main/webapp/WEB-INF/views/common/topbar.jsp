<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<header class="topbar">
    <div class="topbar-left">
        <!-- Mobile Sidebar Toggle -->
        <button class="icon-btn" id="sidebarToggleBtn" style="display: none;" onclick="document.getElementById('appSidebar').classList.toggle('open')">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="3" y1="12" x2="21" y2="12"></line>
                <line x1="3" y1="6" x2="21" y2="6"></line>
                <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
        </button>

        <h1 class="page-title"><c:out value="${pageTitle != null ? pageTitle : 'Dashboard'}"/></h1>
    </div>

    <div class="topbar-right">
        <!-- Live Synchronized Telemetry Clock -->
        <div class="live-clock-badge">
            <span class="pulse-dot"></span>
            <span id="topbarClock">--:--:--</span>
        </div>

        <!-- Alert Notification Bell with Count Badge -->
        <a href="${pageContext.request.contextPath}/alerts" class="icon-btn" title="View Alerts">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"></path>
            </svg>
            <c:if test="${unreadAlertsCount > 0}">
                <span class="alert-badge"><c:out value="${unreadAlertsCount}"/></span>
            </c:if>
        </a>

        <!-- Light / Dark Theme Switcher Button -->
        <button class="icon-btn theme-toggle-btn" title="Toggle Light/Dark Theme">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
            </svg>
        </button>

        <!-- User Profile Quick Link -->
        <a href="${pageContext.request.contextPath}/profile" class="icon-btn" title="Account Settings" style="background: var(--gradient-sunrise); color:#fff; font-weight:700;">
            <c:out value="${sessionScope.currentUser != null ? sessionScope.currentUser.name.substring(0,1).toUpperCase() : 'U'}"/>
        </a>
    </div>
</header>
