package com.solar.dao.impl;

import com.solar.dao.StorageStatusDao;
import com.solar.exception.DataAccessException;
import com.solar.model.StorageStatus;
import com.solar.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of StorageStatusDao with upsert capabilities.
 */
public class StorageStatusDaoImpl implements StorageStatusDao {
    private static final Logger logger = LoggerFactory.getLogger(StorageStatusDaoImpl.class);

    private final DBConnection dbConnection;

    public StorageStatusDaoImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public Optional<StorageStatus> findByDeviceId(Long deviceId) {
        String sql = "SELECT s.id, s.device_id, s.charge_percent, s.state, s.health_percent, s.updated_at, d.device_code, d.name AS device_name " +
                     "FROM storage_status s JOIN devices d ON s.device_id = d.id WHERE s.device_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, deviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToStorage(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding storage status for device: {}", deviceId, e);
            throw new DataAccessException("Failed to query battery storage status", e);
        }
        return Optional.empty();
    }

    @Override
    public List<StorageStatus> findByOwnerId(Long ownerId) {
        List<StorageStatus> list = new ArrayList<>();
        String sql = "SELECT s.id, s.device_id, s.charge_percent, s.state, s.health_percent, s.updated_at, d.device_code, d.name AS device_name " +
                     "FROM storage_status s JOIN devices d ON s.device_id = d.id " +
                     "WHERE d.owner_id = ? ORDER BY s.id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToStorage(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding storage units for owner: {}", ownerId, e);
            throw new DataAccessException("Failed to query owner storage units", e);
        }
        return list;
    }

    @Override
    public boolean upsert(StorageStatus status) {
        // First check if record exists
        Optional<StorageStatus> existing = findByDeviceId(status.getDeviceId());
        if (existing.isPresent()) {
            String updateSql = "UPDATE storage_status SET charge_percent = ?, state = ?, health_percent = ?, updated_at = ? WHERE device_id = ?";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, status.getChargePercent());
                ps.setString(2, status.getState());
                ps.setDouble(3, status.getHealthPercent());
                ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                ps.setLong(5, status.getDeviceId());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                logger.error("Error updating storage status for device: {}", status.getDeviceId(), e);
                throw new DataAccessException("Failed to update battery storage status", e);
            }
        } else {
            String insertSql = "INSERT INTO storage_status (device_id, charge_percent, state, health_percent, updated_at) VALUES (?, ?, ?, ?, ?)";
            try (Connection conn = dbConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, status.getDeviceId());
                ps.setDouble(2, status.getChargePercent());
                ps.setString(3, status.getState());
                ps.setDouble(4, status.getHealthPercent());
                ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
                int affected = ps.executeUpdate();
                if (affected > 0) {
                    try (ResultSet gk = ps.getGeneratedKeys()) {
                        if (gk.next()) status.setId(gk.getLong(1));
                    }
                    return true;
                }
                return false;
            } catch (SQLException e) {
                logger.error("Error inserting storage status for device: {}", status.getDeviceId(), e);
                throw new DataAccessException("Failed to insert battery storage status", e);
            }
        }
    }

    private StorageStatus mapResultSetToStorage(ResultSet rs) throws SQLException {
        StorageStatus s = new StorageStatus();
        s.setId(rs.getLong("id"));
        s.setDeviceId(rs.getLong("device_id"));
        s.setDeviceCode(rs.getString("device_code"));
        s.setDeviceName(rs.getString("device_name"));
        s.setChargePercent(rs.getDouble("charge_percent"));
        s.setState(rs.getString("state"));
        s.setHealthPercent(rs.getDouble("health_percent"));
        Timestamp ts = rs.getTimestamp("updated_at");
        if (ts != null) s.setUpdatedAt(ts.toLocalDateTime());
        return s;
    }
}
