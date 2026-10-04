package com.solar.servlet;

import com.solar.model.Alert;
import com.solar.model.EnergyDevice;
import com.solar.model.User;
import com.solar.service.*;
import com.solar.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Executive Administrative Overview Dashboard.
 * Assembles system-wide KPIs, overall production/consumption datasets, device health, and users.
 */
@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {

    private UserService userService;
    private DeviceService deviceService;
    private EnergyService energyService;
    private AlertService alertService;
    private SettingsService settingsService;

    @Override
    public void init() {
        this.userService = new UserService();
        this.deviceService = new DeviceService();
        this.energyService = new EnergyService();
        this.alertService = new AlertService();
        this.settingsService = new SettingsService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. KPI Counts
        long activeUsers = userService.getActiveUserCount();
        long activeDevices = deviceService.getActiveDeviceCount();
        long totalDevices = deviceService.getTotalDeviceCount();
        Map<String, Object> summary = energyService.getSystemDashboardMetrics();

        // 2. Data Lists
        List<EnergyDevice> devices = deviceService.getAllDevices();
        List<User> users = userService.getAllUsers();
        List<Alert> recentAlerts = alertService.getRecentGlobalAlerts(8);
        Map<String, String> settings = settingsService.getAllSettings();

        // 3. Hourly Aggregations for Chart.js
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> hourlyData = energyService.getHourlyTelemetry(null, null, startOfToday, now);

        req.setAttribute("activeUsers", activeUsers);
        req.setAttribute("activeDevices", activeDevices);
        req.setAttribute("totalDevices", totalDevices);
        req.setAttribute("systemSummary", summary);
        req.setAttribute("devices", devices);
        req.setAttribute("users", users);
        req.setAttribute("recentAlerts", recentAlerts);
        req.setAttribute("settings", settings);
        req.setAttribute("chartHourlyJson", JsonUtil.toJson(hourlyData));

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
