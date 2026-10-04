package com.solar.servlet;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.exception.ValidationException;
import com.solar.model.EnergyDevice;
import com.solar.model.User;
import com.solar.service.DeviceService;
import com.solar.service.UserService;
import com.solar.util.Constants;
import com.solar.util.DateUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Administrative Controller for Energy Device Registration, Monitoring, and Allocation.
 */
@WebServlet(name = "DeviceServlet", urlPatterns = "/admin/devices")
public class DeviceServlet extends HttpServlet {

    private DeviceService deviceService;
    private UserService userService;

    @Override
    public void init() {
        this.deviceService = new DeviceService();
        this.userService = new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<EnergyDevice> devices = deviceService.getAllDevices();
        List<User> users = userService.getAllUsers();

        req.setAttribute("devices", devices);
        req.setAttribute("users", users);
        req.setAttribute("deviceTypes", DeviceType.values());
        req.setAttribute("deviceStatuses", DeviceStatus.values());

        req.getRequestDispatcher("/WEB-INF/views/admin/devices.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        String action = req.getParameter("action");

        try {
            if ("create".equalsIgnoreCase(action)) {
                String code = req.getParameter("deviceCode");
                String name = req.getParameter("name");
                DeviceType type = DeviceType.fromString(req.getParameter("type"));
                String location = req.getParameter("location");
                DeviceStatus status = DeviceStatus.fromString(req.getParameter("status"));
                Double capacityKw = Double.parseDouble(req.getParameter("capacityKw"));
                String ownerStr = req.getParameter("ownerId");
                Long ownerId = (ownerStr != null && !ownerStr.trim().isEmpty()) ? Long.parseLong(ownerStr) : null;
                LocalDate installedOn = DateUtil.parseDate(req.getParameter("installedOn"));

                deviceService.registerDevice(code, name, type, location, status, capacityKw, ownerId, installedOn);
                session.setAttribute(Constants.FLASH_SUCCESS, "Device '" + name + "' [" + code + "] registered successfully.");

            } else if ("update".equalsIgnoreCase(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                String name = req.getParameter("name");
                DeviceType type = DeviceType.fromString(req.getParameter("type"));
                String location = req.getParameter("location");
                DeviceStatus status = DeviceStatus.fromString(req.getParameter("status"));
                Double capacityKw = Double.parseDouble(req.getParameter("capacityKw"));
                String ownerStr = req.getParameter("ownerId");
                Long ownerId = (ownerStr != null && !ownerStr.trim().isEmpty()) ? Long.parseLong(ownerStr) : null;
                LocalDate installedOn = DateUtil.parseDate(req.getParameter("installedOn"));

                deviceService.updateDevice(id, name, type, location, status, capacityKw, ownerId, installedOn);
                session.setAttribute(Constants.FLASH_SUCCESS, "Device '" + name + "' updated successfully.");

            } else if ("updateStatus".equalsIgnoreCase(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                DeviceStatus status = DeviceStatus.fromString(req.getParameter("status"));
                deviceService.updateDeviceStatus(id, status);
                session.setAttribute(Constants.FLASH_SUCCESS, "Device status updated to " + status.getLabel() + ".");

            } else if ("delete".equalsIgnoreCase(action)) {
                Long id = Long.parseLong(req.getParameter("id"));
                deviceService.deleteDevice(id);
                session.setAttribute(Constants.FLASH_SUCCESS, "Device removed from system.");
            }
        } catch (ValidationException ve) {
            session.setAttribute(Constants.FLASH_ERROR, ve.getMessage());
        } catch (Exception e) {
            session.setAttribute(Constants.FLASH_ERROR, "Failed to process device action: " + e.getMessage());
        }

        // Post-Redirect-Get pattern
        resp.sendRedirect(req.getContextPath() + "/admin/devices");
    }
}
