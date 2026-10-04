<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty sessionScope.flashSuccess}">
    <div class="toast-container">
        <div class="toast toast-success">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-emerald)" stroke-width="2">
                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                <polyline points="22 4 12 14.01 9 11.01"></polyline>
            </svg>
            <span class="toast-text"><c:out value="${sessionScope.flashSuccess}"/></span>
            <button class="close-btn" onclick="this.closest('.toast').remove()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
            </button>
        </div>
    </div>
    <c:remove var="flashSuccess" scope="session"/>
</c:if>

<c:if test="${not empty sessionScope.flashError}">
    <div class="toast-container">
        <div class="toast toast-error">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="var(--solar-rose)" stroke-width="2">
                <circle cx="12" cy="12" r="10"></circle>
                <line x1="12" y1="8" x2="12" y2="12"></line>
                <line x1="12" y1="16" x2="12.01" y2="16"></line>
            </svg>
            <span class="toast-text"><c:out value="${sessionScope.flashError}"/></span>
            <button class="close-btn" onclick="this.closest('.toast').remove()">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
            </button>
        </div>
    </div>
    <c:remove var="flashError" scope="session"/>
</c:if>
