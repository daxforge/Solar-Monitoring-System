<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="User Management" scope="request"/>
<c:set var="activeNav" value="users" scope="request"/>

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
                        <h2 style="font-size: 1.25rem;">Registered User Directory</h2>
                        <p style="font-size: 0.85rem;">Manage system administrators and homeowner accounts.</p>
                    </div>

                    <div style="display:flex; align-items:center; gap: 0.75rem;">
                        <div class="search-input-wrap">
                            <svg class="search-icon" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                            <input type="text" class="table-search-input" data-table-target="usersTable" placeholder="Search name or email...">
                        </div>
                        <button type="button" class="btn btn-primary btn-sm" data-modal-target="createUserModal">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                            Add New User
                        </button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="custom-table" id="usersTable">
                        <thead>
                            <tr>
                                <th>User ID</th>
                                <th>Full Name</th>
                                <th>Email Address</th>
                                <th>Assigned Role</th>
                                <th>Account Status</th>
                                <th>Created Date</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="u" items="${users}">
                                <tr>
                                    <td>#<c:out value="${u.id}"/></td>
                                    <td><strong><c:out value="${u.name}"/></strong></td>
                                    <td><c:out value="${u.email}"/></td>
                                    <td>
                                        <span class="status-chip ${u.role == 'ADMIN' ? 'chip-maintenance' : 'chip-online'}">
                                            <c:out value="${u.role.displayName}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <span class="status-chip ${u.active ? 'chip-online' : 'chip-offline'}">
                                            <span class="chip-dot"></span>
                                            <c:out value="${u.active ? 'Active' : 'Disabled'}"/>
                                        </span>
                                    </td>
                                    <td><c:out value="${u.createdAt != null ? u.createdAt.toLocalDate() : 'N/A'}"/></td>
                                    <td>
                                        <div class="actions-cell" style="justify-content: flex-end;">
                                            <button type="button" class="btn btn-secondary btn-sm"
                                                    onclick="openEditUserModal('${u.id}', '<c:out value="${u.name}"/>', '<c:out value="${u.email}"/>', '${u.role}', ${u.active})">
                                                Edit
                                            </button>

                                            <c:if test="${sessionScope.currentUser.id != u.id}">
                                                <form action="${pageContext.request.contextPath}/admin/users" method="post" style="display:inline;" onsubmit="return confirm('Permanently remove user \'${u.name}\'? Associated devices will be unassigned.');">
                                                    <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${u.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                                </form>
                                            </c:if>
                                        </div>
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

<!-- Modal: Create User -->
<div class="modal-overlay" id="createUserModal">
    <div class="modal-window">
        <div class="modal-header">
            <h3 class="modal-title">Provision New Account</h3>
            <button class="close-btn" data-modal-close>&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users" method="post">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="create">

            <div class="modal-body">
                <div style="margin-bottom: 1rem;">
                    <label for="createName">Full Name</label>
                    <input type="text" id="createName" name="name" placeholder="e.g. John Doe" required>
                </div>
                <div style="margin-bottom: 1rem;">
                    <label for="createEmail">Email Address</label>
                    <input type="email" id="createEmail" name="email" placeholder="john@domain.com" required>
                </div>
                <div style="margin-bottom: 1rem;">
                    <label for="createPassword">Initial Password</label>
                    <input type="password" id="createPassword" name="password" placeholder="Minimum 6 characters" required>
                </div>
                <div>
                    <label for="createRole">System Role</label>
                    <select id="createRole" name="role" required>
                        <option value="HOMEOWNER" selected>Homeowner (Solar &amp; Storage Monitoring)</option>
                        <option value="ADMIN">Administrator (Full System Control)</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Create User</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit User -->
<div class="modal-overlay" id="editUserModal">
    <div class="modal-window">
        <div class="modal-header">
            <h3 class="modal-title">Edit User Profile</h3>
            <button class="close-btn" data-modal-close>&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/users" method="post">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" id="editUserId">

            <div class="modal-body">
                <div style="margin-bottom: 1rem;">
                    <label for="editName">Full Name</label>
                    <input type="text" id="editName" name="name" required>
                </div>
                <div style="margin-bottom: 1rem;">
                    <label for="editEmail">Email Address</label>
                    <input type="email" id="editEmail" name="email" required>
                </div>
                <div style="margin-bottom: 1rem;">
                    <label for="editRole">System Role</label>
                    <select id="editRole" name="role" required>
                        <option value="HOMEOWNER">Homeowner</option>
                        <option value="ADMIN">Administrator</option>
                    </select>
                </div>
                <div>
                    <label for="editActive">Account State</label>
                    <select id="editActive" name="active">
                        <option value="true">Active</option>
                        <option value="false">Suspended / Inactive</option>
                    </select>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </div>
        </form>
    </div>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp"/>

<script>
    function openEditUserModal(id, name, email, role, active) {
        document.getElementById('editUserId').value = id;
        document.getElementById('editName').value = name;
        document.getElementById('editEmail').value = email;
        document.getElementById('editRole').value = role;
        document.getElementById('editActive').value = active.toString();
        document.getElementById('editUserModal').classList.add('active');
    }
</script>
