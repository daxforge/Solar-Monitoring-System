package com.solar.servlet;

import com.solar.model.User;
import com.solar.service.EnergyService;
import com.solar.service.SettingsService;
import com.solar.util.Constants;
import com.solar.util.DateUtil;
import com.solar.util.JsonUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controller for Tracking Energy Consumption Reports, Peak Hours and Tariff Costs.
 */
@WebServlet(name = "ConsumptionServlet", urlPatterns = "/homeowner/consumption")
public class ConsumptionServlet extends HttpServlet {

    private EnergyService energyService;
    private SettingsService settingsService;

    @Override
    public void init() {
        this.energyService = new EnergyService();
        this.settingsService = new SettingsService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);
        Long ownerId = currentUser.getId();

        String period = req.getParameter("period");
        if (period == null || period.trim().isEmpty()) {
            period = "7days";
        }
        String customFrom = req.getParameter("from");
        String customTo = req.getParameter("to");

        // Generate report rows
        List<Map<String, Object>> reportRows = energyService.getConsumptionReport(ownerId, period, customFrom, customTo);

        // Calculate KPI totals
        double totalKwh = reportRows.stream()
                .mapToDouble(r -> ((Number) r.getOrDefault("totalKwh", 0.0)).doubleValue())
                .sum();
        totalKwh = Math.round(totalKwh * 100.0) / 100.0;

        double tariff = settingsService.getDoubleSetting(Constants.SETTING_TARIFF_PER_KWH, Constants.DEFAULT_TARIFF_PER_KWH);
        double totalCost = Math.round(totalKwh * tariff * 100.0) / 100.0;

        // Chart data for daily/hourly consumption trends
        LocalDateTime[] dateRange = DateUtil.resolveDateRange(period, customFrom, customTo);
        List<Map<String, Object>> chartData;
        if ("today".equalsIgnoreCase(period)) {
            chartData = energyService.getHourlyTelemetry(ownerId, null, dateRange[0], dateRange[1]);
        } else {
            chartData = energyService.getDailyTelemetry(ownerId, null, dateRange[0], dateRange[1]);
        }

        req.setAttribute("selectedPeriod", period);
        req.setAttribute("customFrom", customFrom);
        req.setAttribute("customTo", customTo);
        req.setAttribute("reportRows", reportRows);
        req.setAttribute("totalKwh", totalKwh);
        req.setAttribute("totalCost", totalCost);
        req.setAttribute("tariff", tariff);
        req.setAttribute("chartConsumptionJson", JsonUtil.toJson(chartData));

        req.getRequestDispatcher("/WEB-INF/views/homeowner/consumption.jsp").forward(req, resp);
    }
}
