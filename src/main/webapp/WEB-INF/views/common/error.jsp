<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>System Notice | Helios Solar</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
    <style>
        .error-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
            text-align: center;
            background: var(--gradient-sun-glow), var(--bg-app);
        }
        .error-card {
            max-width: 520px;
            width: 100%;
            background: var(--bg-surface);
            border: 1px solid var(--border-subtle);
            border-radius: var(--radius-lg);
            padding: 2.5rem;
            box-shadow: var(--shadow-lg);
        }
        .error-code {
            font-family: var(--font-heading);
            font-size: 5rem;
            font-weight: 700;
            background: var(--gradient-sunrise);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            line-height: 1;
            margin-bottom: 1rem;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-card fade-in">
            <div class="error-code">${pageContext.errorData.statusCode != 0 ? pageContext.errorData.statusCode : "404"}</div>
            <h2>Resource Unavailable</h2>
            <p style="margin: 1rem 0 1.75rem;">
                The energy resource, device telemetry stream, or route you requested could not be resolved.
            </p>
            <div style="display:flex; justify-content:center; gap: 1rem;">
                <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Return to System</a>
                <a href="javascript:history.back()" class="btn btn-secondary">Go Back</a>
            </div>
        </div>
    </div>
</body>
</html>
