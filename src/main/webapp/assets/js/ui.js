/**
 * Solar Energy Monitoring System - UI Interactions & Animations
 * Features: Light/Dark Theme Switcher, Live Clock, Modal Dialogs, Table Search,
 * KPI Count-Up Animation, Toast Notifications, and Canvas Particle Simulation.
 */

document.addEventListener('DOMContentLoaded', () => {
    initTheme();
    initClock();
    initModals();
    initTableSearch();
    initCountUp();
    initCanvasParticles();
});

/* ==========================================================================
   1. Theme Toggle (Light / Dark Mode)
   ========================================================================== */
function initTheme() {
    const savedTheme = localStorage.getItem('solar_theme') || 'light';
    applyTheme(savedTheme);

    const toggleBtns = document.querySelectorAll('.theme-toggle-btn');
    toggleBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            const current = document.documentElement.getAttribute('data-theme') || 'light';
            const next = current === 'dark' ? 'light' : 'dark';
            applyTheme(next);
            localStorage.setItem('solar_theme', next);
        });
    });
}

function applyTheme(theme) {
    if (theme === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    } else {
        document.documentElement.removeAttribute('data-theme');
    }
    // Dispatch custom event so Chart.js can update color palette
    window.dispatchEvent(new CustomEvent('solarThemeChanged', { detail: { theme } }));
}

/* ==========================================================================
   2. Live Topbar Clock
   ========================================================================== */
function initClock() {
    const clockEl = document.getElementById('topbarClock');
    if (!clockEl) return;

    function update() {
        const now = new Date();
        const options = { hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false };
        clockEl.textContent = now.toLocaleTimeString([], options);
    }
    update();
    setInterval(update, 1000);
}

/* ==========================================================================
   3. Accessible Modal Dialogs
   ========================================================================== */
function initModals() {
    // Open modal triggers
    document.querySelectorAll('[data-modal-target]').forEach(trigger => {
        trigger.addEventListener('click', (e) => {
            e.preventDefault();
            const targetId = trigger.getAttribute('data-modal-target');
            const modal = document.getElementById(targetId);
            if (modal) openModal(modal);
        });
    });

    // Close modal triggers
    document.querySelectorAll('.modal-overlay .close-btn, .modal-overlay [data-modal-close]').forEach(btn => {
        btn.addEventListener('click', () => {
            const modal = btn.closest('.modal-overlay');
            if (modal) closeModal(modal);
        });
    });

    // Close on backdrop click
    document.querySelectorAll('.modal-overlay').forEach(overlay => {
        overlay.addEventListener('click', (e) => {
            if (e.target === overlay) closeModal(overlay);
        });
    });

    // Close on Escape key
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            document.querySelectorAll('.modal-overlay.active').forEach(closeModal);
        }
    });
}

function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal(modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

/* ==========================================================================
   4. Instant Table Search & Filtering
   ========================================================================== */
function initTableSearch() {
    const searchInputs = document.querySelectorAll('.table-search-input');
    searchInputs.forEach(input => {
        const targetTableId = input.getAttribute('data-table-target');
        const table = document.getElementById(targetTableId);
        if (!table) return;

        input.addEventListener('input', () => {
            const query = input.value.toLowerCase().trim();
            const rows = table.querySelectorAll('tbody tr');

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(query) ? '' : 'none';
            });
        });
    });
}

/* ==========================================================================
   5. KPI Metric Count-Up Animation
   ========================================================================== */
function initCountUp() {
    const numbers = document.querySelectorAll('.count-up');
    numbers.forEach(el => {
        const target = parseFloat(el.getAttribute('data-target') || el.textContent);
        if (isNaN(target)) return;

        const duration = 900;
        const steps = 40;
        const stepTime = duration / steps;
        let current = 0;
        const increment = target / steps;
        const isDecimal = target % 1 !== 0;

        const timer = setInterval(() => {
            current += increment;
            if ((increment > 0 && current >= target) || (increment < 0 && current <= target)) {
                el.textContent = isDecimal ? target.toFixed(1) : Math.round(target);
                clearInterval(timer);
            } else {
                el.textContent = isDecimal ? current.toFixed(1) : Math.round(current);
            }
        }, stepTime);
    });
}

/* ==========================================================================
   6. Toast Notifications
   ========================================================================== */
function showToast(message, type = 'success') {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <span class="toast-text">${message}</span>
        <button class="close-btn" style="padding:2px;" onclick="this.parentElement.remove()">
            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
        </button>
    `;
    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 300ms ease';
        setTimeout(() => toast.remove(), 300);
    }, 4500);
}

/* ==========================================================================
   7. Subtle Sun-Rays & Floating Solar Photons (Pure Canvas)
   ========================================================================== */
function initCanvasParticles() {
    const canvas = document.getElementById('heroParticleCanvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');
    let width = canvas.width = canvas.parentElement.offsetWidth;
    let height = canvas.height = canvas.parentElement.offsetHeight;

    window.addEventListener('resize', () => {
        if (!canvas.parentElement) return;
        width = canvas.width = canvas.parentElement.offsetWidth;
        height = canvas.height = canvas.parentElement.offsetHeight;
    });

    const particles = [];
    const count = 35;

    for (let i = 0; i < count; i++) {
        particles.push({
            x: Math.random() * width,
            y: Math.random() * height,
            radius: Math.random() * 2.2 + 0.8,
            speedY: -(Math.random() * 0.4 + 0.15),
            speedX: (Math.random() - 0.5) * 0.3,
            opacity: Math.random() * 0.5 + 0.2,
            amber: Math.random() > 0.4
        });
    }

    function render() {
        ctx.clearRect(0, 0, width, height);

        // Soft sun beam radial glow
        const grad = ctx.createRadialGradient(width * 0.8, height * 0.1, 10, width * 0.8, height * 0.1, width * 0.6);
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        grad.addColorStop(0, isDark ? 'rgba(245, 158, 11, 0.12)' : 'rgba(245, 158, 11, 0.18)');
        grad.addColorStop(0.5, isDark ? 'rgba(251, 113, 133, 0.04)' : 'rgba(251, 113, 133, 0.08)');
        grad.addColorStop(1, 'transparent');
        ctx.fillStyle = grad;
        ctx.fillRect(0, 0, width, height);

        // Photons
        for (let p of particles) {
            ctx.beginPath();
            ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
            ctx.fillStyle = p.amber ? `rgba(245, 158, 11, ${p.opacity})` : `rgba(251, 113, 133, ${p.opacity})`;
            ctx.fill();

            p.y += p.speedY;
            p.x += p.speedX;

            if (p.y < -10) {
                p.y = height + 10;
                p.x = Math.random() * width;
            }
            if (p.x < -10) p.x = width + 10;
            if (p.x > width + 10) p.x = -10;
        }

        requestAnimationFrame(render);
    }
    render();
}
