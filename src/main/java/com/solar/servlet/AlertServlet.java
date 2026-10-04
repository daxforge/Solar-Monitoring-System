package com.solar.servlet;

import com.solar.enums.Role;
import com.solar.model.Alert;
import com.solar.model.User;
import com.solar.service.AlertService;
import com.solar.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

/**
 * Controller for managing system alerts, safety event logs, and notification reads.
 */
@WebServlet(name = "AlertServlet", urlPatterns = "/alerts")
public class AlertServlet extends HttpServlet {

    private AlertService alertService;

    @Override
    public void init() {
        this.alertService = new AlertService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);

        String filter = req.getParameter("filter");
        List<Alert> alerts;

        if (currentUser.getRole() == Role.ADMIN) {
            alerts = alertService.getRecentGlobalAlerts(50);
        } else {
            if ("unread".equalsIgnoreCase(filter)) {
                alerts = alertService.getUnreadUserAlerts(currentUser.getId());
            } else {
                alerts = alertService.getUserAlerts(currentUser.getId(), 50);
            }
        }

        int unreadCount = alertService.getUnreadCount(currentUser.getId());

        req.setAttribute("alerts", alerts);
        req.setAttribute("currentFilter", filter != null ? filter : "all");
        req.setAttribute("unreadCount", unreadCount);

        req.getRequestDispatcher("/WEB-INF/views/homeowner/alerts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        String action = req.getParameter("action");

        try {
            if ("markRead".equalsIgnoreCase(action)) {
                Long alertId = Long.parseLong(req.getParameter("alertId"));
                alertService.markAsRead(alertId, currentUser.getId());
                session.setAttribute(Constants.FLASH_SUCCESS, "Alert marked as read.");
            } else if ("markAllRead".equalsIgnoreCase(action)) {
                alertService.markAllAsRead(currentUser.getId());
                session.setAttribute(Constants.FLASH_SUCCESS, "All alerts marked as read.");
            }
        } catch (Exception e) {
            session.setAttribute(Constants.FLASH_ERROR, "Failed to update alert: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/alerts");
    }
}
