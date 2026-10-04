package com.solar.dao.impl;

import com.solar.dao.UserDao;
import com.solar.enums.Role;
import com.solar.exception.DataAccessException;
import com.solar.model.User;
import com.solar.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of UserDao utilizing PreparedStatement and connection pooling.
 * Demonstrates transactional integrity (commit/rollback) and try-with-resources.
 */
public class UserDaoImpl implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    private final DBConnection dbConnection;

    public UserDaoImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT id, name, email, password_hash, role, created_at, active FROM users WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", id, e);
            throw new DataAccessException("Failed to query user by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password_hash, role, created_at, active FROM users WHERE email = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email.trim().toLowerCase());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
            throw new DataAccessException("Failed to query user by email", e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email, password_hash, role, created_at, active FROM users ORDER BY id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all users", e);
            throw new DataAccessException("Failed to query all users", e);
        }
        return list;
    }

    @Override
    public List<User> search(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id, name, email, password_hash, role, created_at, active FROM users " +
                     "WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ? ORDER BY name ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String pattern = "%" + (keyword == null ? "" : keyword.trim().toLowerCase()) + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToUser(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching users with keyword: {}", keyword, e);
            throw new DataAccessException("Failed to search users", e);
        }
        return list;
    }

    @Override
    public boolean create(User user) {
        String sql = "INSERT INTO users (name, email, password_hash, role, created_at, active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getPasswordHash());
            ps.setString(4, user.getRole().name());
            ps.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            ps.setBoolean(6, user.isActive());

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error inserting user: {}", user.getEmail(), e);
            throw new DataAccessException("Failed to persist new user", e);
        }
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ?, active = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail().trim().toLowerCase());
            ps.setString(3, user.getRole().name());
            ps.setBoolean(4, user.isActive());
            ps.setLong(5, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating user: {}", user.getId(), e);
            throw new DataAccessException("Failed to update user details", e);
        }
    }

    @Override
    public boolean updatePassword(Long userId, String passwordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setLong(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating password for user ID: {}", userId, e);
            throw new DataAccessException("Failed to update user password", e);
        }
    }

    /**
     * Atomically deletes a user while releasing associated devices and cleaning alerts.
     * Demonstrates JDBC transaction management with explicit commit and rollback.
     */
    @Override
    public boolean deleteWithCascade(Long userId) {
        Connection conn = null;
        try {
            conn = dbConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            // 1. Release device associations
            try (PreparedStatement psDevices = conn.prepareStatement("UPDATE devices SET owner_id = NULL WHERE owner_id = ?")) {
                psDevices.setLong(1, userId);
                psDevices.executeUpdate();
            }

            // 2. Clear alerts targeting this user
            try (PreparedStatement psAlerts = conn.prepareStatement("DELETE FROM alerts WHERE user_id = ?")) {
                psAlerts.setLong(1, userId);
                psAlerts.executeUpdate();
            }

            // 3. Delete the user record
            int affected;
            try (PreparedStatement psUser = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
                psUser.setLong(1, userId);
                affected = psUser.executeUpdate();
            }

            conn.commit(); // Commit Transaction
            logger.info("Transaction committed: User ID {} deleted and dependencies released.", userId);
            return affected > 0;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on failure
                    logger.warn("Transaction rolled back for user deletion: {}", userId);
                } catch (SQLException rbEx) {
                    logger.error("Error rolling back transaction", rbEx);
                }
            }
            throw new DataAccessException("Failed to cascade delete user ID " + userId, e);
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

    @Override
    public long countUsers() {
        String sql = "SELECT count(*) FROM users WHERE active = TRUE";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting users", e);
            throw new DataAccessException("Failed to count active users", e);
        }
        return 0;
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(Role.fromString(rs.getString("role")));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            user.setCreatedAt(ts.toLocalDateTime());
        }
        user.setActive(rs.getBoolean("active"));
        return user;
    }
}
