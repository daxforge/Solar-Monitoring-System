package com.solar.service;

import com.solar.dao.DeviceDao;
import com.solar.dao.EnergyReadingDao;
import com.solar.dao.StorageStatusDao;
import com.solar.dao.impl.DeviceDaoImpl;
import com.solar.dao.impl.EnergyReadingDaoImpl;
import com.solar.dao.impl.StorageStatusDaoImpl;
import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.model.Battery;
import com.solar.model.EnergyDevice;
import com.solar.model.EnergyReading;
import com.solar.model.StorageStatus;
import com.solar.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Core business service for solar energy telemetry, chart data preparation, and consumption analytics.
 */
public class EnergyService {
    private static final Logger logger = LoggerFactory.getLogger(EnergyService.class);

    private final EnergyReadingDao energyReadingDao;
    private final DeviceDao deviceDao;
    private final StorageStatusDao storageStatusDao;
    private final AlertService alertService;
    private final SettingsService settingsService;

    public EnergyService() {
        this.energyReadingDao = new EnergyReadingDaoImpl();
        this.deviceDao = new DeviceDaoImpl();
        this.storageStatusDao = new StorageStatusDaoImpl();
        this.alertService = new AlertService();
        this.settingsService = new SettingsService();
    }

    public EnergyService(EnergyReadingDao energyReadingDao, DeviceDao deviceDao,
                         StorageStatusDao storageStatusDao, AlertService alertService,
                         SettingsService settingsService) {
        this.energyReadingDao = energyReadingDao;
        this.deviceDao = deviceDao;
        this.storageStatusDao = storageStatusDao;
        this.alertService = alertService;
        this.settingsService = settingsService;
    }

    /**
     * Records a new telemetry reading and checks automated threshold rules.
     */
    public EnergyReading recordReading(Long deviceId, Double prodKw, Double consKw, Double battPct) {
        EnergyReading reading = new EnergyReading(null, deviceId, LocalDateTime.now(), prodKw, consKw, battPct);
        energyReadingDao.insert(reading);

        // Update battery storage status if applicable
        Optional<EnergyDevice> deviceOpt = deviceDao.findById(deviceId);
        if (deviceOpt.isPresent()) {
            EnergyDevice device = deviceOpt.get();
            if (device.getType() == DeviceType.BATTERY && battPct != null) {
                String state = prodKw > consKw ? "CHARGING" : (consKw > 0.1 ? "DISCHARGING" : "IDLE");
                storageStatusDao.upsert(new StorageStatus(null, deviceId, battPct, state, 98.0, LocalDateTime.now()));
            }
            // Trigger Alert Engine
            alertService.evaluateTelemetryThresholds(device, reading);
        }

        return reading;
    }

    public Optional<EnergyReading> getLatestReading(Long deviceId) {
        return energyReadingDao.findLatestByDeviceId(deviceId);
    }

    public List<EnergyReading> getRecentReadings(Long deviceId, int limit) {
        return energyReadingDao.findRecentByDeviceId(deviceId, limit);
    }

    public List<Map<String, Object>> getHourlyTelemetry(Long ownerId, Long deviceId, LocalDateTime start, LocalDateTime end) {
        return energyReadingDao.getAggregatedByHour(ownerId, deviceId, start, end);
    }

    public List<Map<String, Object>> getDailyTelemetry(Long ownerId, Long deviceId, LocalDateTime start, LocalDateTime end) {
        return energyReadingDao.getAggregatedByDay(ownerId, deviceId, start, end);
    }

    /**
     * Generates a consumption report with tariff calculations.
     */
    public List<Map<String, Object>> getConsumptionReport(Long ownerId, String period, String customFrom, String customTo) {
        LocalDateTime[] range = DateUtil.resolveDateRange(period, customFrom, customTo);
        List<Map<String, Object>> rows = energyReadingDao.getConsumptionReport(ownerId, range[0], range[1]);

        double tariff = settingsService.getDoubleSetting("tariff_per_kwh", 0.18);
        for (Map<String, Object> row : rows) {
            double totalKwh = (double) row.get("totalKwh");
            double estimatedCost = totalKwh * tariff;
            row.put("estimatedCost", Math.round(estimatedCost * 100.0) / 100.0);
            row.put("tariff", tariff);
        }
        return rows;
    }

    public Map<String, Object> getSystemDashboardMetrics() {
        return energyReadingDao.getSystemSummaryToday();
    }

    public Map<String, Object> getHomeownerDashboardMetrics(Long ownerId) {
        return energyReadingDao.getHomeownerSummaryToday(ownerId);
    }

    public List<StorageStatus> getBatteryStatusesForOwner(Long ownerId) {
        return storageStatusDao.findByOwnerId(ownerId);
    }

    public Optional<StorageStatus> getStorageStatusByDeviceId(Long deviceId) {
        return storageStatusDao.findByDeviceId(deviceId);
    }
}
