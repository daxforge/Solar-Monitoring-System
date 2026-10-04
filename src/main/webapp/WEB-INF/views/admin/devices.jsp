<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Hardware Device Registry" scope="request"/>
<c:set var="activeNav" value="devices" scope="request"/>

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
                        <h2 style="font-size: 1.25rem;">Solar &amp; Storage Device Fleet</h2>
                        <p style="font-size: 0.85rem;">Register photovoltaic panels, inverters, storage batteries, and smart meters.</p>
                    </div>

                    <div style="display:flex; align-items:center; gap: 0.75rem;">
                        <div class="search-input-wrap">
                            <svg class="search-icon" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
                            <input type="text" class="table-search-input" data-table-target="deviceFleetTable" placeholder="Filter devices or codes...">
                        </div>
                        <button type="button" class="btn btn-primary btn-sm" data-modal-target="registerDeviceModal">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                            Register Device
                        </button>
                    </div>
                </div>

                <div class="table-responsive">
                    <table class="custom-table" id="deviceFleetTable">
                        <thead>
                            <tr>
                                <th>Code</th>
                                <th>Device Name</th>
                                <th>Type</th>
                                <th>Installation Location</th>
                                <th>Capacity</th>
                                <th>Assigned Homeowner</th>
                                <th>Status</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${devices}">
                                <tr>
                                    <td style="font-family: monospace; font-weight:700;"><c:out value="${d.deviceCode}"/></td>
                                    <td><strong><c:out value="${d.name}"/></strong></td>
                                    <td><c:out value="${d.type.displayName}"/></td>
                                    <td><c:out value="${d.location}"/></td>
                                    <td><c:out value="${d.capacityKw}"/> kW</td>
                                    <td><c:out value="${d.ownerName != null ? d.ownerName : 'Unassigned'}"/></td>
                                    <td>
                                        <span class="status-chip ${d.status.cssClass}">
                                            <span class="chip-dot"></span>
                                            <c:out value="${d.status.label}"/>
                                        </span>
                                    </td>
                                    <td>
                                        <div class="actions-cell" style="justify-content: flex-end;">
                                            <button type="button" class="btn btn-secondary btn-sm"
                                                    onclick="openEditDeviceModal('${d.id}', '<c:out value="${d.deviceCode}"/>', '<c:out value="${d.name}"/>', '${d.type}', '<c:out value="${d.location}"/>', '${d.status}', '${d.capacityKw}', '${d.ownerId}', '${d.installedOn}')">
                                                Edit
                                            </button>

                                            <!-- Quick Toggle Status Button -->
                                            <form action="${pageContext.request.contextPath}/admin/devices" method="post" style="display:inline;">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                <input type="hidden" name="action" value="updateStatus">
                                                <input type="hidden" name="id" value="${d.id}">
                                                <input type="hidden" name="status" value="${d.status == 'ONLINE' ? 'OFFLINE' : 'ONLINE'}">
                                                <button type="submit" class="btn btn-secondary btn-sm" title="Toggle Online/Offline">
                                                    <c:out value="${d.status == 'ONLINE' ? 'Set Offline' : 'Set Online'}"/>
                                                </button>
                                            </form>

                                            <form action="${pageContext.request.contextPath}/admin/devices" method="post" style="display:inline;" onsubmit="return confirm('Permanently remove device \'${d.name}\'?');">
                                                <input type="hidden" name="csrfToken" value="${csrfToken}">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="id" value="${d.id}">
                                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                                            </form>
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

<!-- Modal: Register Device -->
<div class="modal-overlay" id="registerDeviceModal">
    <div class="modal-window">
        <div class="modal-header">
            <h3 class="modal-title">Register Energy Hardware</h3>
            <button class="close-btn" data-modal-close>&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/devices" method="post">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="create">

            <div class="modal-body">
                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1rem; margin-bottom: 1rem;">
                    <div>
                        <label for="createDevCode">Device Identifier</label>
                        <input type="text" id="createDevCode" name="deviceCode" placeholder="DEV-SP-005" required>
                    </div>
                    <div>
                        <label for="createDevName">Display Name</label>
                        <input type="text" id="createDevName" name="name" placeholder="Rooftop East Array" required>
                    </div>
                </div>

                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1rem; margin-bottom: 1rem;">
                    <div>
                        <label for="createDevType">Hardware Type</label>
                        <select id="createDevType" name="type" required>
                            <c:forEach var="t" items="${deviceTypes}">
                                <option value="${t}"><c:out value="${t.displayName}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="createCapacity">Rated Capacity (kW)</label>
                        <input type="number" step="0.1" min="0.1" id="createCapacity" name="capacityKw" value="5.0" required>
                    </div>
                </div>

                <div style="margin-bottom: 1rem;">
                    <label for="createLocation">Physical Location</label>
                    <input type="text" id="createLocation" name="location" placeholder="e.g. South Villa Roof" required>
                </div>

                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1rem;">
                    <div>
                        <label for="createOwner">Assign Homeowner</label>
                        <select id="createOwner" name="ownerId">
                            <option value="">-- Unassigned Grid Unit --</option>
                            <c:forEach var="u" items="${users}">
                                <c:if test="${u.role == 'HOMEOWNER'}">
                                    <option value="${u.id}"><c:out value="${u.name}"/> (<c:out value="${u.email}"/>)</option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="createStatus">Initial Status</label>
                        <select id="createStatus" name="status">
                            <c:forEach var="s" items="${deviceStatuses}">
                                <option value="${s}"><c:out value="${s.label}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-modal-close>Cancel</button>
                <button type="submit" class="btn btn-primary">Complete Registration</button>
            </div>
        </form>
    </div>
</div>

<!-- Modal: Edit Device -->
<div class="modal-overlay" id="editDeviceModal">
    <div class="modal-window">
        <div class="modal-header">
            <h3 class="modal-title">Edit Hardware Configuration</h3>
            <button class="close-btn" data-modal-close>&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/admin/devices" method="post">
            <input type="hidden" name="csrfToken" value="${csrfToken}">
            <input type="hidden" name="action" value="update">
            <input type="hidden" name="id" id="editDevId">

            <div class="modal-body">
                <div style="margin-bottom: 1rem;">
                    <label for="editDevName">Device Name</label>
                    <input type="text" id="editDevName" name="name" required>
                </div>

                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1rem; margin-bottom: 1rem;">
                    <div>
                        <label for="editDevType">Hardware Type</label>
                        <select id="editDevType" name="type" required>
                            <c:forEach var="t" items="${deviceTypes}">
                                <option value="${t}"><c:out value="${t.displayName}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="editCapacity">Rated Capacity (kW)</label>
                        <input type="number" step="0.1" min="0.1" id="editCapacity" name="capacityKw" required>
                    </div>
                </div>

                <div style="margin-bottom: 1rem;">
                    <label for="editLocation">Physical Location</label>
                    <input type="text" id="editLocation" name="location" required>
                </div>

                <div style="display:grid; grid-template-columns: 1fr 1fr; gap:1rem;">
                    <div>
                        <label for="editOwner">Assign Homeowner</label>
                        <select id="editOwner" name="ownerId">
                            <option value="">-- Unassigned --</option>
                            <c:forEach var="u" items="${users}">
                                <c:if test="${u.role == 'HOMEOWNER'}">
                                    <option value="${u.id}"><c:out value="${u.name}"/></option>
                                </c:if>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="editStatus">Operational Status</label>
                        <select id="editStatus" name="status">
                            <c:forEach var="s" items="${deviceStatuses}">
                                <option value="${s}"><c:out value="${s.label}"/></option>
                            </c:forEach>
                        </select>
                    </div>
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
    function openEditDeviceModal(id, code, name, type, location, status, capacity, ownerId, installedOn) {
        document.getElementById('editDevId').value = id;
        document.getElementById('editDevName').value = name;
        document.getElementById('editDevType').value = type;
        document.getElementById('editLocation').value = location;
        document.getElementById('editStatus').value = status;
        document.getElementById('editCapacity').value = capacity;
        document.getElementById('editOwner').value = ownerId || '';
        document.getElementById('editDeviceModal').classList.add('active');
    }
</script>
