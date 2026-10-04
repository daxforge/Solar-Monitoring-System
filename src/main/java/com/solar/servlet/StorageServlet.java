package com.solar.servlet;

import com.solar.enums.DeviceType;
import com.solar.model.EnergyDevice;
import com.solar.model.StorageStatus;
import com.solar.model.User;
import com.solar.service.DeviceService;
import com.solar.service.EnergyService;
import com.solar.util.Constants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for Battery Energy Storage System (BESS) Management.
 * Renders state-of-charge gauge, health percentage, capacity, and autonomy time estimates.
 */
@WebServlet(name = "StorageServlet", urlPatterns = "/homeowner/storage")
public class StorageServlet extends HttpServlet {

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

        // Retrieve battery devices
        List<EnergyDevice> devices = deviceService.getDevicesByOwner(ownerId);
        List<EnergyDevice> batteryDevices = devices.stream()
                .filter(d -> d.getType() == DeviceType.BATTERY)
                .collect(Collectors.toList());

        // Determine selected battery
        String deviceIdParam = req.getParameter("deviceId");
        EnergyDevice selectedDevice = null;
        if (deviceIdParam != null && !deviceIdParam.trim().isEmpty()) {
            try {
                Long devId = Long.parseLong(deviceIdParam);
                selectedDevice = batteryDevices.stream().filter(d -> d.getId().equals(devId)).findFirst().orElse(null);
            } catch (Exception ignored) {}
        }

        if (selectedDevice == null && !batteryDevices.isEmpty()) {
            selectedDevice = batteryDevices.get(0);
        }

        StorageStatus status = null;
        if (selectedDevice != null) {
            status = energyService.getStorageStatusByDeviceId(selectedDevice.getId()).orElse(null);
        }

        req.setAttribute("batteryDevices", batteryDevices);
        req.setAttribute("selectedDevice", selectedDevice);
        req.setAttribute("storageStatus", status);

        req.getRequestDispatcher("/WEB-INF/views/homeowner/storage.jsp").forward(req, resp);
    }
}
