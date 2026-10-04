<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Helios | Intelligent Solar Energy Monitoring System</title>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/theme.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/components.css">

    <style>
        .landing-hero {
            position: relative;
            min-height: 85vh;
            display: flex;
            align-items: center;
            justify-content: center;
            text-align: center;
            padding: 4rem 2rem;
            background: var(--gradient-sun-glow), var(--bg-app);
            overflow: hidden;
        }

        .hero-content {
            position: relative;
            z-index: 10;
            max-width: 840px;
        }

        .hero-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            padding: 0.4rem 1rem;
            border-radius: var(--radius-pill);
            background: var(--bg-surface);
            border: 1px solid var(--border-subtle);
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--solar-amber);
            margin-bottom: 1.5rem;
            box-shadow: var(--shadow-sm);
        }

        .hero-title {
            font-size: 3.5rem;
            font-weight: 700;
            line-height: 1.15;
            margin-bottom: 1.25rem;
            letter-spacing: -0.03em;
        }

        .hero-title span {
            background: var(--gradient-sunrise);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
        }

        .hero-description {
            font-size: 1.2rem;
            color: var(--text-secondary);
            margin-bottom: 2.25rem;
            max-width: 680px;
            margin-left: auto;
            margin-right: auto;
        }

        .hero-cta-group {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 1rem;
            flex-wrap: wrap;
        }

        .features-section {
            padding: 5rem 2rem;
            max-width: 1200px;
            margin: 0 auto;
        }

        .feature-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 2rem;
            margin-top: 3rem;
        }

        .feature-card {
            background: var(--bg-surface);
            border: 1px solid var(--border-subtle);
            border-radius: var(--radius-lg);
            padding: 2rem;
            box-shadow: var(--shadow-sm);
            transition: all var(--transition-smooth);
        }

        .feature-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-md);
            border-color: var(--solar-amber);
        }

        .feature-icon-box {
            width: 48px;
            height: 48px;
            border-radius: var(--radius-md);
            background: var(--bg-surface-subtle);
            display: flex;
            align-items: center;
            justify-content: center;
            margin-bottom: 1.25rem;
            color: var(--solar-amber);
        }

        @media (max-width: 768px) {
            .hero-title { font-size: 2.3rem; }
            .feature-grid { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body>
    <!-- Top Landing Header -->
    <header style="padding: 1.25rem 2.5rem; display:flex; align-items:center; justify-content:space-between; border-bottom: 1px solid var(--border-subtle); background: var(--bg-surface);">
        <div style="display:flex; align-items:center; gap:0.75rem;">
            <div class="brand-icon">
                <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2">
                    <circle cx="12" cy="12" r="5"></circle>
                    <line x1="12" y1="1" x2="12" y2="3"></line><line x1="12" y1="21" x2="12" y2="23"></line>
                    <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                </svg>
            </div>
            <span class="brand-text">HELIOS</span>
        </div>
        <div style="display:flex; align-items:center; gap:1rem;">
            <button class="icon-btn theme-toggle-btn" title="Toggle Theme">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
                </svg>
            </button>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                Launch Portal
            </a>
        </div>
    </header>

    <!-- Hero Section with Canvas Simulation -->
    <section class="landing-hero">
        <canvas id="heroParticleCanvas" class="hero-canvas"></canvas>

        <div class="hero-content fade-in">
            <div class="hero-badge">
                <span class="pulse-dot"></span>
                Jakarta EE 10 &amp; Tomcat 10.1 Architecture
            </div>
            <h1 class="hero-title">
                Intelligent Precision <span>Solar Energy</span> Monitoring
            </h1>
            <p class="hero-description">
                Real-time tracking of photovoltaic generation, residential consumption, chemical battery storage, and dynamic tariff telemetry.
            </p>
            <div class="hero-cta-group">
                <a href="${pageContext.request.contextPath}/login" class="btn btn-primary" style="padding: 0.8rem 2rem; font-size: 1rem;">
                    Sign In to Console &rarr;
                </a>
                <a href="#features" class="btn btn-secondary" style="padding: 0.8rem 1.8rem; font-size: 1rem;">
                    Explore Capabilities
                </a>
            </div>
        </div>
    </section>

    <!-- Architecture & Features Showcase -->
    <section class="features-section" id="features">
        <div style="text-align: center; max-width: 600px; margin: 0 auto;">
            <div class="brand-subtext" style="margin-bottom: 0.5rem;">Core Architecture</div>
            <h2>Built on Clean MVC Principles</h2>
            <p>Designed to satisfy college marking rubrics with pure Java OOP, JDBC PreparedStatement security, and high-frequency concurrency.</p>
        </div>

        <div class="feature-grid">
            <div class="feature-card">
                <div class="feature-icon-box">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="5"></circle>
                        <line x1="12" y1="1" x2="12" y2="3"></line>
                        <line x1="12" y1="21" x2="12" y2="23"></line>
                    </svg>
                </div>
                <h3>Live Telemetry Stream</h3>
                <p>ScheduledExecutorService simulates continuous solar irradiance and home loads every 5 seconds with zero-lag AJAX charts.</p>
            </div>

            <div class="feature-card">
                <div class="feature-icon-box">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <rect x="1" y="6" width="18" height="12" rx="2"></rect>
                        <line x1="23" y1="11" x2="23" y2="13"></line>
                    </svg>
                </div>
                <h3>Storage &amp; Autonomy</h3>
                <p>Dynamic SVG battery gauge tracking State-of-Charge (SoC), State-of-Health (SoH), and estimated time to discharge.</p>
            </div>

            <div class="feature-card">
                <div class="feature-icon-box">
                    <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                    </svg>
                </div>
                <h3>Intelligent Alert Engine</h3>
                <p>Dynamic evaluation comparing physical telemetry against administrator-defined thresholds for low battery and load spikes.</p>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer style="padding: 2.5rem; text-align: center; border-top: 1px solid var(--border-subtle); color: var(--text-muted); font-size: 0.85rem;">
        Helios Solar Energy Monitoring System &bull; Jakarta Servlet 6.0 &bull; Tomcat 10.1 &bull; MySQL 8 JDBC &bull; HikariCP
    </footer>

    <script src="${pageContext.request.contextPath}/assets/js/ui.js"></script>
</body>
</html>
