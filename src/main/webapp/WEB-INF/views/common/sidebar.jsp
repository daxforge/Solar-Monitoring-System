<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<aside class="sidebar" id="appSidebar">
    <!-- Brand Header -->
    <div class="sidebar-header">
        <div class="brand-icon">
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="5"></circle>
                <line x1="12" y1="1" x2="12" y2="3"></line>
                <line x1="12" y1="21" x2="12" y2="23"></line>
                <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                <line x1="1" y1="12" x2="3" y2="12"></line>
                <line x1="21" y1="12" x2="23" y2="12"></line>
                <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
                <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
            </svg>
        </div>
        <div>
            <div class="brand-text">HELIOS</div>
            <div class="brand-subtext">Solar Energy OS</div>
        </div>
    </div>

    <!-- Navigation Links -->
    <nav class="sidebar-nav">
        <c:choose>
            <%-- ADMIN NAVIGATION --%>
            <c:when test="${sessionScope.currentUser.role == 'ADMIN'}">
                <div class="nav-section-title">Operations</div>
                <a href="${pageContext.request.contextPath}/admin/dashboard" class="nav-item ${activeNav == 'adminDashboard' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7"></rect><rect x="14" y="3" width="7" height="7"></rect><rect x="14" y="14" width="7" height="7"></rect><rect x="3" y="14" width="7" height="7"></rect></svg>
                    <span>Dashboard</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/users" class="nav-item ${activeNav == 'users' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
                    <span>User Management</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/devices" class="nav-item ${activeNav == 'devices' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><rect x="2" y="2" width="20" height="8" rx="2" ry="2"></rect><rect x="2" y="14" width="20" height="8" rx="2" ry="2"></rect><line x1="6" y1="6" x2="6.01" y2="6"></line><line x1="6" y1="18" x2="6.01" y2="18"></line></svg>
                    <span>Hardware Devices</span>
                </a>
                <a href="${pageContext.request.contextPath}/admin/settings" class="nav-item ${activeNav == 'settings' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"></circle><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path></svg>
                    <span>System Settings</span>
                </a>
                <a href="${pageContext.request.contextPath}/alerts" class="nav-item ${activeNav == 'alerts' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
                    <span>System Alerts</span>
                </a>
            </c:when>

            <%-- HOMEOWNER NAVIGATION --%>
            <c:otherwise>
                <div class="nav-section-title">Energy Management</div>
                <a href="${pageContext.request.contextPath}/homeowner/dashboard" class="nav-item ${activeNav == 'homeDashboard' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path><polyline points="9 22 9 12 15 12 15 22"></polyline></svg>
                    <span>Overview</span>
                </a>
                <a href="${pageContext.request.contextPath}/homeowner/production" class="nav-item ${activeNav == 'production' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><circle cx="12" cy="12" r="5"></circle><line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line><line x1="1" y1="12" x2="3" y2="12"></line><line x1="21" y1="12" x2="23" y2="12"></line><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line></svg>
                    <span>Solar Production</span>
                </a>
                <a href="${pageContext.request.contextPath}/homeowner/consumption" class="nav-item ${activeNav == 'consumption' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"></polyline></svg>
                    <span>Consumption &amp; Costs</span>
                </a>
                <a href="${pageContext.request.contextPath}/homeowner/storage" class="nav-item ${activeNav == 'storage' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><rect x="1" y="6" width="18" height="12" rx="2"></rect><line x1="23" y1="11" x2="23" y2="13"></line></svg>
                    <span>Battery Storage</span>
                </a>
                <a href="${pageContext.request.contextPath}/alerts" class="nav-item ${activeNav == 'alerts' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"></path><path d="M13.73 21a2 2 0 0 1-3.46 0"></path></svg>
                    <span>Notifications</span>
                </a>
                <a href="${pageContext.request.contextPath}/profile" class="nav-item ${activeNav == 'profile' ? 'active' : ''}">
                    <svg viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                    <span>Account Profile</span>
                </a>
            </c:otherwise>
        </c:choose>
    </nav>

    <!-- User Profile Snippet & Logout -->
    <div class="sidebar-footer">
        <div class="user-snippet">
            <div class="user-avatar-mini">
                <c:out value="${sessionScope.currentUser != null ? sessionScope.currentUser.name.substring(0,1).toUpperCase() : 'U'}"/>
            </div>
            <div class="user-meta-mini">
                <div class="user-name-mini"><c:out value="${sessionScope.currentUser.name}"/></div>
                <div class="user-role-mini"><c:out value="${sessionScope.currentUser.role.displayName}"/></div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" title="Sign Out" style="color: var(--text-muted); display:flex; align-items:center;">
                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4"></path>
                    <polyline points="16 17 21 12 16 7"></polyline>
                    <line x1="21" y1="12" x2="9" y2="12"></line>
                </svg>
            </a>
        </div>
    </div>
</aside>
