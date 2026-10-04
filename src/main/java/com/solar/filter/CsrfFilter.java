package com.solar.filter;

import com.solar.util.Constants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

/**
 * Filter generating and verifying Cross-Site Request Forgery (CSRF) tokens on state-changing HTTP requests.
 */
@WebFilter(filterName = "CsrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(true);
        String sessionToken = (String) session.getAttribute(Constants.SESSION_CSRF_TOKEN);
        if (sessionToken == null) {
            sessionToken = UUID.randomUUID().toString().replace("-", "");
            session.setAttribute(Constants.SESSION_CSRF_TOKEN, sessionToken);
        }

        // Expose token to request attributes so JSPs can inject into forms: ${csrfToken}
        req.setAttribute(Constants.SESSION_CSRF_TOKEN, sessionToken);

        String method = req.getMethod().toUpperCase();
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Validate token on POST requests (excluding public login/assets)
        if ("POST".equals(method) && !isCsrfExempt(path)) {
            String requestToken = req.getParameter(Constants.SESSION_CSRF_TOKEN);
            if (requestToken == null || requestToken.isEmpty()) {
                requestToken = req.getHeader("X-CSRF-Token");
            }

            if (requestToken == null || !sessionToken.equals(requestToken.trim())) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                session.setAttribute(Constants.FLASH_ERROR, "Security validation error: Invalid or expired CSRF token.");
                resp.sendRedirect(req.getHeader("Referer") != null ? req.getHeader("Referer") : req.getContextPath() + "/");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isCsrfExempt(String path) {
        if (path == null) return true;
        return path.equals("/login") || path.startsWith("/assets/") || path.startsWith("/api/");
    }
}
