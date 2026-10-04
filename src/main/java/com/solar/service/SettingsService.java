package com.solar.service;

import com.solar.dao.SystemSettingDao;
import com.solar.dao.impl.SystemSettingDaoImpl;
import com.solar.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Service managing global system parameters, tariffs and safety alert thresholds.
 */
public class SettingsService {
    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);

    private final SystemSettingDao settingDao;

    public SettingsService() {
        this.settingDao = new SystemSettingDaoImpl();
    }

    public SettingsService(SystemSettingDao settingDao) {
        this.settingDao = settingDao;
    }

    public String getSetting(String key, String defaultValue) {
        return settingDao.getSetting(key, defaultValue);
    }

    public double getDoubleSetting(String key, double defaultValue) {
        String val = settingDao.getSetting(key, String.valueOf(defaultValue));
        try {
            return Double.parseDouble(val.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public int getIntSetting(String key, int defaultValue) {
        String val = settingDao.getSetting(key, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public Map<String, String> getAllSettings() {
        Map<String, String> map = settingDao.getAllSettings();
        // Ensure defaults are present if database is fresh
        map.putIfAbsent(Constants.SETTING_SYSTEM_NAME, Constants.DEFAULT_SYSTEM_NAME);
        map.putIfAbsent(Constants.SETTING_TARIFF_PER_KWH, String.valueOf(Constants.DEFAULT_TARIFF_PER_KWH));
        map.putIfAbsent(Constants.SETTING_LOW_BATTERY_THRESHOLD, String.valueOf(Constants.DEFAULT_LOW_BATTERY_THRESHOLD));
        map.putIfAbsent(Constants.SETTING_LOW_PRODUCTION_THRESHOLD, String.valueOf(Constants.DEFAULT_LOW_PRODUCTION_THRESHOLD));
        map.putIfAbsent(Constants.SETTING_HIGH_CONSUMPTION_THRESHOLD, String.valueOf(Constants.DEFAULT_HIGH_CONSUMPTION_THRESHOLD));
        map.putIfAbsent(Constants.SETTING_REFRESH_INTERVAL_SEC, String.valueOf(Constants.DEFAULT_REFRESH_INTERVAL_SEC));
        return map;
    }

    public void updateSettings(Map<String, String> settings, Long updatedBy) {
        settingDao.updateBatchSettings(settings, updatedBy);
        logger.info("System settings batch updated by user ID {}", updatedBy);
    }
}
