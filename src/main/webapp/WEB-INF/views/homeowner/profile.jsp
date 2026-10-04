<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Account Profile &amp; Security" scope="request"/>
<c:set var="activeNav" value="profile" scope="request"/>

<jsp:include page="/WEB-INF/views/common/header.jsp"/>

<div class="app-container">
    <jsp:include page="/WEB-INF/views/common/sidebar.jsp"/>

    <main class="main-wrapper">
        <jsp:include page="/WEB-INF/views/common/topbar.jsp"/>
        <jsp:include page="/WEB-INF/views/common/toast.jsp"/>

        <div class="content-body fade-in">
            <div style="max-width: 820px; margin: 0 auto; display:flex; flex-direction:column; gap:2rem;">
                <!-- Profile Identity Card -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-sky)" stroke-width="2">
                                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle>
                            </svg>
                            <span>Personal Information</span>
                        </div>
                        <span class="status-chip chip-online">
                            <c:out value="${user.role.displayName}"/>
                        </span>
                    </div>

                    <form action="${pageContext.request.contextPath}/profile" method="post">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="updateInfo">

                        <div style="margin-bottom: 1.25rem;">
                            <label for="profName">Full Name</label>
                            <input type="text" id="profName" name="name" value="<c:out value="${user.name}"/>" required>
                        </div>

                        <div style="margin-bottom: 1.5rem;">
                            <label for="profEmail">Email Address</label>
                            <input type="email" id="profEmail" name="email" value="<c:out value="${user.email}"/>" required>
                            <span style="font-size:0.75rem; color:var(--text-muted);">Used for account access and critical telemetry alert notifications.</span>
                        </div>

                        <div style="display:flex; justify-content:flex-end;">
                            <button type="submit" class="btn btn-primary">Update Profile Details</button>
                        </div>
                    </form>
                </div>

                <!-- Password & Authentication Security Card -->
                <div class="card">
                    <div class="card-header">
                        <div class="card-title">
                            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-coral)" stroke-width="2">
                                <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect><path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                            </svg>
                            <span>Security Credentials</span>
                        </div>
                        <span class="badge" style="font-size:0.75rem; color:var(--text-muted);">BCrypt 12 Rounds</span>
                    </div>

                    <form action="${pageContext.request.contextPath}/profile" method="post">
                        <input type="hidden" name="csrfToken" value="${csrfToken}">
                        <input type="hidden" name="action" value="changePassword">

                        <div style="margin-bottom: 1.25rem;">
                            <label for="oldPassword">Current Password</label>
                            <input type="password" id="oldPassword" name="oldPassword" placeholder="••••••••" required>
                        </div>

                        <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1rem; margin-bottom: 1.5rem;">
                            <div>
                                <label for="newPassword">New Password</label>
                                <input type="password" id="newPassword" name="newPassword" placeholder="Minimum 6 characters" required>
                            </div>
                            <div>
                                <label for="confirmPassword">Confirm New Password</label>
                                <input type="password" id="confirmPassword" name="confirmPassword" placeholder="Repeat new password" required>
                            </div>
                        </div>

                        <div style="display:flex; justify-content:flex-end;">
                            <button type="submit" class="btn btn-secondary">Update Password</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </main>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>
