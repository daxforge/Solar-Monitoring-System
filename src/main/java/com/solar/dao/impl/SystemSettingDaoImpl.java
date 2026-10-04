package com.solar.dao.impl;

import com.solar.dao.SystemSettingDao;
import com.solar.exception.DataAccessException;
import com.solar.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JDBC implementation of SystemSettingDao with transactional batch updates.
 */
public class SystemSettingDaoImpl implements SystemSettingDao {
    private static final Logger logger = LoggerFactory.getLogger(SystemSettingDaoImpl.class);

    private final DBConnection dbConnection;

    public SystemSettingDaoImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT setting_value FROM system_settings WHERE setting_key = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
            }
        } catch (SQLException e) {
            logger.warn("Could not query setting key '{}', returning default: {}", key, defaultValue);
        }
        return defaultValue;
    }

    @Override
    public Map<String, String> getAllSettings() {
        Map<String, String> map = new HashMap<>();
        String sql = "SELECT setting_key, setting_value FROM system_settings";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("setting_key"), rs.getString("setting_value"));
            }
        } catch (SQLException e) {
            logger.error("Error retrieving all system settings", e);
            throw new DataAccessException("Failed to query settings", e);
        }
        return map;
    }

    @Override
    public boolean updateSetting(String key, String value, Long updatedBy) {
        String sql = "INSERT INTO system_settings (setting_key, setting_value, updated_by, updated_at) " +
                     "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), updated_by = VALUES(updated_by), updated_at = VALUES(updated_at)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, key.trim());
            ps.setString(2, value.trim());
            if (updatedBy != null) ps.setLong(3, updatedBy); else ps.setNull(3, Types.BIGINT);
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating setting '{}'", key, e);
            throw new DataAccessException("Failed to update setting: " + key, e);
        }
    }

    @Override
    public boolean updateBatchSettings(Map<String, String> settings, Long updatedBy) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Transactional batch update

            String sql = "INSERT INTO system_settings (setting_key, setting_value, updated_by, updated_at) " +
                         "VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value), updated_by = VALUES(updated_by), updated_at = VALUES(updated_at)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (Map.Entry<String, String> entry : settings.entrySet()) {
                    ps.setString(1, entry.getKey().trim());
                    ps.setString(2, entry.getValue().trim());
                    if (updatedBy != null) ps.setLong(3, updatedBy); else ps.setNull(3, Types.BIGINT);
                    ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rbEx) {
                    logger.error("Rollback failed", rbEx);
                }
            }
            logger.error("Failed batch settings update", e);
            throw new DataAccessException("Failed to update system settings batch", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    logger.error("Error closing connection", closeEx);
                }
            }
        }
    }
}
