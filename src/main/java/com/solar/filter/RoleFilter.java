package com.solar.filter;

import com.solar.enums.Role;
import com.solar.model.User;
import com.solar.util.Constants;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filter enforcing Role-Based Access Control (RBAC) policies on URL hierarchies.
 */
@WebFilter(filterName = "RoleFilter", urlPatterns = {"/admin/*", "/homeowner/*"})
public class RoleFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute(Constants.SESSION_USER) : null;

        if (user == null) {
            resp.sendRedirect(req.getContextPath() + Constants.URL_LOGIN);
            return;
        }

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // /admin/* requires ADMIN role
        if (path.startsWith("/admin/") && user.getRole() != Role.ADMIN) {
            session.setAttribute(Constants.FLASH_ERROR, "Access Denied: Administrative privileges required.");
            resp.sendRedirect(req.getContextPath() + Constants.URL_HOMEOWNER_DASHBOARD);
            return;
        }

        chain.doFilter(request, response);
    }
}
