package com.solar.dao.impl;

import com.solar.dao.AlertDao;
import com.solar.enums.AlertSeverity;
import com.solar.exception.DataAccessException;
import com.solar.model.Alert;
import com.solar.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of AlertDao.
 */
public class AlertDaoImpl implements AlertDao {
    private static final Logger logger = LoggerFactory.getLogger(AlertDaoImpl.class);

    private final DBConnection dbConnection;

    public AlertDaoImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public boolean create(Alert alert) {
        String sql = "INSERT INTO alerts (user_id, device_id, severity, message, is_read, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, alert.getUserId());
            if (alert.getDeviceId() != null && alert.getDeviceId() > 0) {
                ps.setLong(2, alert.getDeviceId());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setString(3, alert.getSeverity().name());
            ps.setString(4, alert.getMessage());
            ps.setBoolean(5, alert.isRead());
            ps.setTimestamp(6, Timestamp.valueOf(alert.getCreatedAt() != null ? alert.getCreatedAt() : LocalDateTime.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) alert.setId(gk.getLong(1));
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error creating alert: {}", alert.getMessage(), e);
            throw new DataAccessException("Failed to persist alert", e);
        }
    }

    @Override
    public List<Alert> findByUserId(Long userId, int limit) {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT a.id, a.user_id, a.device_id, a.severity, a.message, a.is_read, a.created_at, " +
                     "d.device_code, d.name AS device_name, u.name AS user_name " +
                     "FROM alerts a " +
                     "LEFT JOIN devices d ON a.device_id = d.id " +
                     "LEFT JOIN users u ON a.user_id = u.id " +
                     "WHERE a.user_id = ? ORDER BY a.created_at DESC LIMIT ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAlert(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding alerts for user: {}", userId, e);
            throw new DataAccessException("Failed to query alerts", e);
        }
        return list;
    }

    @Override
    public List<Alert> findUnreadByUserId(Long userId) {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT a.id, a.user_id, a.device_id, a.severity, a.message, a.is_read, a.created_at, " +
                     "d.device_code, d.name AS device_name, u.name AS user_name " +
                     "FROM alerts a " +
                     "LEFT JOIN devices d ON a.device_id = d.id " +
                     "LEFT JOIN users u ON a.user_id = u.id " +
                     "WHERE a.user_id = ? AND a.is_read = FALSE ORDER BY a.created_at DESC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAlert(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding unread alerts for user: {}", userId, e);
            throw new DataAccessException("Failed to query unread alerts", e);
        }
        return list;
    }

    @Override
    public int countUnreadByUserId(Long userId) {
        String sql = "SELECT count(*) FROM alerts WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting unread alerts for user: {}", userId, e);
            throw new DataAccessException("Failed to count unread alerts", e);
        }
        return 0;
    }

    @Override
    public boolean markAsRead(Long alertId, Long userId) {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE id = ? AND user_id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, alertId);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking alert as read: {}", alertId, e);
            throw new DataAccessException("Failed to mark alert as read", e);
        }
    }

    @Override
    public boolean markAllAsRead(Long userId) {
        String sql = "UPDATE alerts SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error marking all alerts as read for user: {}", userId, e);
            throw new DataAccessException("Failed to mark all alerts as read", e);
        }
    }

    @Override
    public List<Alert> findAllRecent(int limit) {
        List<Alert> list = new ArrayList<>();
        String sql = "SELECT a.id, a.user_id, a.device_id, a.severity, a.message, a.is_read, a.created_at, " +
                     "d.device_code, d.name AS device_name, u.name AS user_name " +
                     "FROM alerts a " +
                     "LEFT JOIN devices d ON a.device_id = d.id " +
                     "LEFT JOIN users u ON a.user_id = u.id " +
                     "ORDER BY a.created_at DESC LIMIT ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToAlert(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error querying recent global alerts", e);
            throw new DataAccessException("Failed to query global alerts", e);
        }
        return list;
    }

    private Alert mapResultSetToAlert(ResultSet rs) throws SQLException {
        Alert a = new Alert();
        a.setId(rs.getLong("id"));
        a.setUserId(rs.getLong("user_id"));
        a.setUserName(rs.getString("user_name"));
        Long devId = rs.getLong("device_id");
        if (!rs.wasNull()) a.setDeviceId(devId);
        a.setDeviceCode(rs.getString("device_code"));
        a.setDeviceName(rs.getString("device_name"));
        a.setSeverity(AlertSeverity.fromString(rs.getString("severity")));
        a.setMessage(rs.getString("message"));
        a.setRead(rs.getBoolean("is_read"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) a.setCreatedAt(ts.toLocalDateTime());
        return a;
    }
}
