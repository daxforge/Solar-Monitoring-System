package com.solar.servlet;

import com.solar.enums.Role;
import com.solar.model.StorageStatus;
import com.solar.model.User;
import com.solar.service.AlertService;
import com.solar.service.EnergyService;
import com.solar.util.Constants;
import com.solar.util.JsonUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RESTful Telemetry API Controller streaming real-time JSON metrics to client-side Chart.js instances.
 * Enables live 5-second asynchronous polling without browser page refreshes.
 */
@WebServlet(name = "ApiServlet", urlPatterns = "/api/telemetry")
public class ApiServlet extends HttpServlet {

    private EnergyService energyService;
    private AlertService alertService;

    @Override
    public void init() {
        this.energyService = new EnergyService();
        this.alertService = new AlertService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        resp.setHeader("Pragma", "no-cache");
        resp.setDateHeader("Expires", 0);

        HttpSession session = req.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute(Constants.SESSION_USER) : null;

        Long ownerId = null;
        if (currentUser != null && currentUser.getRole() == Role.HOMEOWNER) {
            ownerId = currentUser.getId();
        } else {
            String ownerParam = req.getParameter("ownerId");
            if (ownerParam != null && !ownerParam.trim().isEmpty()) {
                try {
                    ownerId = Long.parseLong(ownerParam);
                } catch (Exception ignored) {}
            }
        }

        String deviceIdParam = req.getParameter("deviceId");
        Long deviceId = null;
        if (deviceIdParam != null && !deviceIdParam.trim().isEmpty()) {
            try {
                deviceId = Long.parseLong(deviceIdParam);
            } catch (Exception ignored) {}
        }

        // Fetch metrics
        Map<String, Object> summary;
        if (ownerId != null) {
            summary = energyService.getHomeownerDashboardMetrics(ownerId);
        } else {
            summary = energyService.getSystemDashboardMetrics();
        }

        // Fetch recent hourly points for live charts
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime now = LocalDateTime.now();
        List<Map<String, Object>> hourlyData = energyService.getHourlyTelemetry(ownerId, deviceId, startOfToday, now);

        // Fetch battery status if homeowner
        StorageStatus batteryStatus = null;
        if (ownerId != null) {
            List<StorageStatus> batteries = energyService.getBatteryStatusesForOwner(ownerId);
            if (!batteries.isEmpty()) batteryStatus = batteries.get(0);
        }

        int unreadAlerts = (currentUser != null) ? alertService.getUnreadCount(currentUser.getId()) : 0;

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("status", "success");
        responseData.put("timestamp", LocalDateTime.now().toString());
        responseData.put("summary", summary);
        responseData.put("hourly", hourlyData);
        responseData.put("unreadAlerts", unreadAlerts);
        if (batteryStatus != null) {
            Map<String, Object> battMap = new HashMap<>();
            battMap.put("chargePercent", batteryStatus.getChargePercent());
            battMap.put("state", batteryStatus.getState());
            battMap.put("healthPercent", batteryStatus.getHealthPercent());
            battMap.put("timeToEmptyHours", Math.round(batteryStatus.getEstimatedTimeToEmptyHours() * 10.0) / 10.0);
            responseData.put("battery", battMap);
        }

        resp.getWriter().write(JsonUtil.toJson(responseData));
    }
}
