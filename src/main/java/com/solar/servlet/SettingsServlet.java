package com.solar.servlet;

import com.solar.model.User;
import com.solar.service.SettingsService;
import com.solar.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Administrative Controller for configuring global system parameters and alert thresholds.
 */
@WebServlet(name = "SettingsServlet", urlPatterns = "/admin/settings")
public class SettingsServlet extends HttpServlet {

    private SettingsService settingsService;

    @Override
    public void init() {
        this.settingsService = new SettingsService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> settings = settingsService.getAllSettings();
        req.setAttribute("settings", settings);
        req.getRequestDispatcher("/WEB-INF/views/admin/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);

        try {
            Map<String, String> newSettings = new HashMap<>();
            newSettings.put(Constants.SETTING_SYSTEM_NAME, req.getParameter("systemName"));
            newSettings.put(Constants.SETTING_TARIFF_PER_KWH, req.getParameter("tariffPerKwh"));
            newSettings.put(Constants.SETTING_LOW_BATTERY_THRESHOLD, req.getParameter("lowBatteryThreshold"));
            newSettings.put(Constants.SETTING_LOW_PRODUCTION_THRESHOLD, req.getParameter("lowProductionThreshold"));
            newSettings.put(Constants.SETTING_HIGH_CONSUMPTION_THRESHOLD, req.getParameter("highConsumptionThreshold"));
            newSettings.put(Constants.SETTING_REFRESH_INTERVAL_SEC, req.getParameter("refreshIntervalSec"));

            settingsService.updateSettings(newSettings, currentUser != null ? currentUser.getId() : null);
            session.setAttribute(Constants.FLASH_SUCCESS, "System settings updated successfully.");
        } catch (Exception e) {
            session.setAttribute(Constants.FLASH_ERROR, "Failed to update settings: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/admin/settings");
    }
}
