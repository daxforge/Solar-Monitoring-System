package com.solar.service;

import com.solar.dao.AlertDao;
import com.solar.dao.impl.AlertDaoImpl;
import com.solar.enums.AlertSeverity;
import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.model.Alert;
import com.solar.model.EnergyDevice;
import com.solar.model.EnergyReading;
import com.solar.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service orchestrating system alert notifications and automated threshold rule evaluations.
 * Acts as the intelligent safety and anomaly engine.
 */
public class AlertService {
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    private final AlertDao alertDao;
    private final SettingsService settingsService;

    public AlertService() {
        this.alertDao = new AlertDaoImpl();
        this.settingsService = new SettingsService();
    }

    public AlertService(AlertDao alertDao, SettingsService settingsService) {
        this.alertDao = alertDao;
        this.settingsService = settingsService;
    }

    public Alert createAlert(Long userId, Long deviceId, AlertSeverity severity, String message) {
        Alert alert = new Alert(null, userId, deviceId, severity, message, false, LocalDateTime.now());
        alertDao.create(alert);
        logger.info("Alert triggered for User {}: [{}] {}", userId, severity, message);
        return alert;
    }

    public List<Alert> getUserAlerts(Long userId, int limit) {
        return alertDao.findByUserId(userId, limit);
    }

    public List<Alert> getUnreadUserAlerts(Long userId) {
        return alertDao.findUnreadByUserId(userId);
    }

    public int getUnreadCount(Long userId) {
        return alertDao.countUnreadByUserId(userId);
    }

    public void markAsRead(Long alertId, Long userId) {
        alertDao.markAsRead(alertId, userId);
    }

    public void markAllAsRead(Long userId) {
        alertDao.markAllAsRead(userId);
    }

    public List<Alert> getRecentGlobalAlerts(int limit) {
        return alertDao.findAllRecent(limit);
    }

    /**
     * Alert Engine Evaluation: Compares streaming device telemetry against dynamic admin thresholds.
     */
    public void evaluateTelemetryThresholds(EnergyDevice device, EnergyReading reading) {
        if (device == null || reading == null) return;
        Long ownerId = device.getOwnerId();
        if (ownerId == null) return;

        double lowBatteryThreshold = settingsService.getDoubleSetting(Constants.SETTING_LOW_BATTERY_THRESHOLD, Constants.DEFAULT_LOW_BATTERY_THRESHOLD);
        double highConsumptionThreshold = settingsService.getDoubleSetting(Constants.SETTING_HIGH_CONSUMPTION_THRESHOLD, Constants.DEFAULT_HIGH_CONSUMPTION_THRESHOLD);

        // 1. Device Offline Rule
        if (device.getStatus() == DeviceStatus.OFFLINE) {
            String msg = String.format("Device '%s' (%s) is OFFLINE. Telemetry heartbeat lost.",
                    device.getName(), device.getDeviceCode());
            createAlert(ownerId, device.getId(), AlertSeverity.CRITICAL, msg);
            return;
        }

        // 2. Battery Storage Threshold Rule
        if (device.getType() == DeviceType.BATTERY && reading.getBatteryPercent() != null) {
            double batt = reading.getBatteryPercent();
            if (batt > 0.0 && batt <= lowBatteryThreshold) {
                String msg = String.format("Battery '%s' reserve is critically low at %.1f%% (threshold: %.0f%%).",
                        device.getName(), batt, lowBatteryThreshold);
                createAlert(ownerId, device.getId(), AlertSeverity.CRITICAL, msg);
            }
        }

        // 3. High Consumption Load Spike Rule
        if (reading.getConsumptionKw() != null && reading.getConsumptionKw() >= highConsumptionThreshold) {
            String msg = String.format("High consumption spike detected on '%s': %.2f kW (threshold: %.1f kW).",
                    device.getName(), reading.getConsumptionKw(), highConsumptionThreshold);
            createAlert(ownerId, device.getId(), AlertSeverity.WARNING, msg);
        }

        // 4. Solar Generation Anomaly (Daytime production deficiency)
        int hour = reading.getReadingTime().getHour();
        if (device.getType() == DeviceType.SOLAR_PANEL && hour >= 10 && hour <= 15) {
            double expectedMin = device.getCapacityKw() * 0.25;
            if (reading.getProductionKw() != null && reading.getProductionKw() < expectedMin) {
                String msg = String.format("Solar array '%s' output (%.2f kW) dropped below expected daylight irradiance.",
                        device.getName(), reading.getProductionKw());
                createAlert(ownerId, device.getId(), AlertSeverity.WARNING, msg);
            }
        }
    }
}
