package com.solar.servlet;

import com.solar.enums.DeviceType;
import com.solar.model.EnergyDevice;
import com.solar.model.EnergyReading;
import com.solar.model.User;
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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for Real-Time Solar Energy Production Monitoring.
 */
@WebServlet(name = "ProductionServlet", urlPatterns = "/homeowner/production")
public class ProductionServlet extends HttpServlet {

    private DeviceService deviceService;
    private EnergyService energyService;

    @Override
    public void init() {
        this.deviceService = new DeviceService();
        this.energyService = new EnergyService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        User currentUser = (User) session.getAttribute(Constants.SESSION_USER);

        Long ownerId = currentUser.getId();
        List<EnergyDevice> devices = deviceService.getDevicesByOwner(ownerId);

        // Filter solar panels (or show all devices if none)
        List<EnergyDevice> solarDevices = devices.stream()
                .filter(d -> d.getType() == DeviceType.SOLAR_PANEL || d.getType() == DeviceType.INVERTER)
                .collect(Collectors.toList());

        if (solarDevices.isEmpty()) {
            solarDevices = devices;
        }

        // Determine selected device
        String deviceIdParam = req.getParameter("deviceId");
        EnergyDevice selectedDevice = null;
        if (deviceIdParam != null && !deviceIdParam.trim().isEmpty()) {
            try {
                Long devId = Long.parseLong(deviceIdParam);
                selectedDevice = solarDevices.stream().filter(d -> d.getId().equals(devId)).findFirst().orElse(null);
            } catch (Exception ignored) {}
        }

        if (selectedDevice == null && !solarDevices.isEmpty()) {
            selectedDevice = solarDevices.get(0);
        }

        double currentKw = 0.0;
        double todayKwh = 0.0;
        List<EnergyReading> recentReadings = List.of();
        List<Map<String, Object>> hourlyData = List.of();

        if (selectedDevice != null) {
            Optional<EnergyReading> latest = energyService.getLatestReading(selectedDevice.getId());
            if (latest.isPresent()) {
                currentKw = latest.get().getProductionKw();
            }
            recentReadings = energyService.getRecentReadings(selectedDevice.getId(), 15);

            LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
            LocalDateTime now = LocalDateTime.now();
            hourlyData = energyService.getHourlyTelemetry(ownerId, selectedDevice.getId(), startOfDay, now);

            // Compute today's total production kWh
            todayKwh = hourlyData.stream()
                    .mapToDouble(m -> ((Number) m.getOrDefault("productionKw", 0.0)).doubleValue())
                    .average().orElse(0.0) * 8.0;
            todayKwh = Math.round(todayKwh * 10.0) / 10.0;
        }

        req.setAttribute("devices", solarDevices);
        req.setAttribute("selectedDevice", selectedDevice);
        req.setAttribute("currentKw", Math.round(currentKw * 100.0) / 100.0);
        req.setAttribute("todayKwh", todayKwh);
        req.setAttribute("recentReadings", recentReadings);
        req.setAttribute("chartHourlyJson", JsonUtil.toJson(hourlyData));

        req.getRequestDispatcher("/WEB-INF/views/homeowner/production.jsp").forward(req, resp);
    }
}
