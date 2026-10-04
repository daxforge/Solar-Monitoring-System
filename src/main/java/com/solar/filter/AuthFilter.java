package com.solar.filter;

import com.solar.model.User;
import com.solar.util.Constants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter verifying that incoming requests to protected application areas have an authenticated session.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = "/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // Allow public paths
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Verify active session user
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(Constants.SESSION_USER) : null;

        if (user == null) {
            // Store redirect URL and redirect to login
            if (!req.getMethod().equalsIgnoreCase("GET")) {
                resp.sendRedirect(req.getContextPath() + Constants.URL_LOGIN);
            } else {
                req.getSession(true).setAttribute("redirectAfterLogin", path);
                resp.sendRedirect(req.getContextPath() + Constants.URL_LOGIN);
            }
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicPath(String path) {
        if (path == null || path.isEmpty() || path.equals("/") || path.equals("/index.jsp")) {
            return true;
        }
        if (path.startsWith("/assets/") || path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/img/")) {
            return true;
        }
        return path.equals("/login") || path.equals("/logout") || path.startsWith("/WEB-INF/views/common/error");
    }
}
