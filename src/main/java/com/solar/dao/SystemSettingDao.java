package com.solar.dao;

import java.util.Map;

/**
 * Data Access Object interface for Global System Configuration Settings.
 */
public interface SystemSettingDao {
    String getSetting(String key, String defaultValue);
    Map<String, String> getAllSettings();
    boolean updateSetting(String key, String value, Long updatedBy);
    boolean updateBatchSettings(Map<String, String> settings, Long updatedBy);
}
