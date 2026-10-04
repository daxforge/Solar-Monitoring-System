<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In | Helios Solar Energy OS</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">
    
    <style>
        .login-wrapper {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem;
            position: relative;
            background: var(--bg-app);
            overflow: hidden;
        }

        .login-card {
            position: relative;
            z-index: 10;
            max-width: 440px;
            width: 100%;
            background: var(--bg-surface);
            border: 1px solid var(--border-subtle);
            border-radius: var(--radius-lg);
            padding: 2.5rem;
            box-shadow: var(--shadow-lg);
            backdrop-filter: blur(16px);
        }

        .login-brand {
            display: flex;
            align-items: center;
            gap: 0.75rem;
            margin-bottom: 1.5rem;
        }

        .login-brand .brand-icon {
            width: 44px;
            height: 44px;
        }

        .demo-credentials-box {
            background-color: var(--bg-surface-subtle);
            border: 1px dashed var(--border-subtle);
            border-radius: var(--radius-md);
            padding: 1rem;
            margin-top: 1.5rem;
            font-size: 0.8rem;
        }

        .demo-btn-group {
            display: flex;
            gap: 0.5rem;
            margin-top: 0.5rem;
        }
    </style>
</head>
<body>
    <div class="login-wrapper">
        <!-- Pure Canvas Particle / Sun-rays Simulator -->
        <canvas id="heroParticleCanvas" class="hero-canvas"></canvas>

        <div class="login-card fade-in">
            <!-- Brand -->
            <div class="login-brand">
                <div class="brand-icon">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
                        <circle cx="12" cy="12" r="5"></circle>
                        <line x1="12" y1="1" x2="12" y2="3"></line>
                        <line x1="12" y1="21" x2="12" y2="23"></line>
                        <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                        <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                        <line x1="1" y1="12" x2="3" y2="12"></line>
                        <line x1="21" y1="12" x2="23" y2="12"></line>
                    </svg>
                </div>
                <div>
                    <h2 style="font-size: 1.45rem; line-height: 1;">HELIOS</h2>
                    <span style="font-size: 0.75rem; color: var(--solar-amber); font-weight: 700; letter-spacing: 0.08em;">SOLAR OS</span>
                </div>
            </div>

            <h3 style="margin-bottom: 0.35rem;">Welcome back</h3>
            <p style="margin-bottom: 1.5rem;">Access real-time telemetry, storage and load telemetry.</p>

            <!-- Alerts / Errors -->
            <c:if test="${not empty errorMessage}">
                <div class="toast toast-error" style="position:static; margin-bottom: 1.25rem; width:100%; box-shadow:none;">
                    <span class="toast-text"><c:out value="${errorMessage}"/></span>
                </div>
            </c:if>

            <c:if test="${not empty logoutMessage}">
                <div class="toast toast-success" style="position:static; margin-bottom: 1.25rem; width:100%; box-shadow:none;">
                    <span class="toast-text"><c:out value="${logoutMessage}"/></span>
                </div>
            </c:if>

            <!-- Login Form -->
            <form action="${pageContext.request.contextPath}/login" method="post" id="loginForm">
                <input type="hidden" name="csrfToken" value="${csrfToken}">

                <div style="margin-bottom: 1.25rem;">
                    <label for="emailInput">Email Address</label>
                    <input type="email" id="emailInput" name="email" value="<c:out value='${enteredEmail != null ? enteredEmail : ""}'/>" placeholder="name@domain.com" required autocomplete="username">
                </div>

                <div style="margin-bottom: 1.5rem;">
                    <label for="passwordInput">Password</label>
                    <input type="password" id="passwordInput" name="password" placeholder="••••••••" required autocomplete="current-password">
                </div>

                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 0.75rem;">
                    Authenticate Session
                </button>
            </form>

            <!-- Quick Demo Auto-Fill (For evaluators and viva presentation) -->
            <div class="demo-credentials-box">
                <div style="font-weight: 700; color: var(--text-main); margin-bottom: 0.25rem;">1-Click Evaluator Credentials:</div>
                <div class="demo-btn-group">
                    <button type="button" class="btn btn-secondary btn-sm" onclick="fillCredentials('admin@solar.local', 'admin123')">
                        Admin Login
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm" onclick="fillCredentials('alice@solar.local', 'user123')">
                        Homeowner (Alice)
                    </button>
                    <button type="button" class="btn btn-secondary btn-sm" onclick="fillCredentials('bob@solar.local', 'user123')">
                        Homeowner (Bob)
                    </button>
                </div>
            </div>

            <div style="text-align: center; margin-top: 1.5rem;">
                <a href="${pageContext.request.contextPath}/" style="font-size: 0.85rem; color: var(--text-muted);">
                    &larr; Back to Landing Page
                </a>
            </div>
        </div>
    </div>

    <script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
    <script>
        function fillCredentials(email, pwd) {
            document.getElementById('emailInput').value = email;
            document.getElementById('passwordInput').value = pwd;
            document.getElementById('loginForm').submit();
        }
    </script>
</body>
</html>
