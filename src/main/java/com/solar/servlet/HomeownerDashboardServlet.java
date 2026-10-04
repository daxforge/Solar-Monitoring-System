package com.solar.servlet;

import com.solar.model.*;
import com.solar.service.AlertService;
import com.solar.service.DeviceService;
import com.solar.service.EnergyService;
import com.solar.util.Constants;
import com.solar.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for the Homeowner Dashboard: energy generation, household load, battery gauge, and alerts.
 */
@WebServlet(name = "HomeownerDashboardServlet", urlPatterns = "/homeowner/dashboard")
public class HomeownerDashboardServlet extends HttpServlet {

    private DeviceService deviceService;
    private EnergyService energyService;
    private AlertService alertService;

    @Override
    public void init() {
        this.deviceService = new DeviceService();
        this.energyService = new EnergyService();
        this.alertService = new AlertService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);

        Long ownerId = currentUser.getId();

        // 1. KPI Telemetry Summary
        Map<String, Object> summary = energyService.getHomeownerDashboardMetrics(ownerId);

        // 2. Hardware Devices
        List<EnergyDevice> devices = deviceService.getDevicesByOwner(ownerId);

        // 3. Battery Storage Status
        List<StorageStatus> batteries = energyService.getBatteryStatusesForOwner(ownerId);
        StorageStatus primaryBattery = batteries.isEmpty() ? null : batteries.get(0);

        // 4. Alerts
        int unreadAlerts = alertService.getUnreadCount(ownerId);
        List<Alert> alerts = alertService.getUserAlerts(ownerId, 5);

        // 5. Hourly Chart Data (Today)
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> hourlyData = energyService.getHourlyTelemetry(ownerId, null, startOfToday, now);

        req.setAttribute("summary", summary);
        req.setAttribute("devices", devices);
        req.setAttribute("primaryBattery", primaryBattery);
        req.setAttribute("unreadAlertsCount", unreadAlerts);
        req.setAttribute("recentAlerts", alerts);
        req.setAttribute("chartHourlyJson", JsonUtil.toJson(hourlyData));

        req.getRequestDispatcher("/WEB-INF/views/homeowner/dashboard.jsp").forward(req, resp);
    }
}
