package com.solar.dao.impl;

import com.solar.dao.DeviceDao;
import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.exception.DataAccessException;
import com.solar.model.Device;
import com.solar.model.EnergyDevice;
import com.solar.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of DeviceDao using polymorphic instantiations.
 */
public class DeviceDaoImpl implements DeviceDao {
    private static final Logger logger = LoggerFactory.getLogger(DeviceDaoImpl.class);

    private final DBConnection dbConnection;

    public DeviceDaoImpl() {
        this.dbConnection = DBConnection.getInstance();
    }

    @Override
    public Optional<EnergyDevice> findById(Long id) {
        String sql = "SELECT d.id, d.device_code, d.name, d.type, d.location, d.status, d.capacity_kw, d.owner_id, d.installed_on, u.name AS owner_name " +
                     "FROM devices d LEFT JOIN users u ON d.owner_id = u.id WHERE d.id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDevice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding device by ID: {}", id, e);
            throw new DataAccessException("Failed to query device by ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<EnergyDevice> findByCode(String deviceCode) {
        String sql = "SELECT d.id, d.device_code, d.name, d.type, d.location, d.status, d.capacity_kw, d.owner_id, d.installed_on, u.name AS owner_name " +
                     "FROM devices d LEFT JOIN users u ON d.owner_id = u.id WHERE d.device_code = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, deviceCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToDevice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding device by code: {}", deviceCode, e);
            throw new DataAccessException("Failed to query device by code", e);
        }
        return Optional.empty();
    }

    @Override
    public List<EnergyDevice> findAll() {
        List<EnergyDevice> list = new ArrayList<>();
        String sql = "SELECT d.id, d.device_code, d.name, d.type, d.location, d.status, d.capacity_kw, d.owner_id, d.installed_on, u.name AS owner_name " +
                     "FROM devices d LEFT JOIN users u ON d.owner_id = u.id ORDER BY d.id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToDevice(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all devices", e);
            throw new DataAccessException("Failed to query all devices", e);
        }
        return list;
    }

    @Override
    public List<EnergyDevice> findByOwnerId(Long ownerId) {
        List<EnergyDevice> list = new ArrayList<>();
        String sql = "SELECT d.id, d.device_code, d.name, d.type, d.location, d.status, d.capacity_kw, d.owner_id, d.installed_on, u.name AS owner_name " +
                     "FROM devices d LEFT JOIN users u ON d.owner_id = u.id WHERE d.owner_id = ? ORDER BY d.id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ownerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDevice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding devices by owner ID: {}", ownerId, e);
            throw new DataAccessException("Failed to query devices by owner", e);
        }
        return list;
    }

    @Override
    public List<EnergyDevice> findByType(DeviceType type) {
        List<EnergyDevice> list = new ArrayList<>();
        String sql = "SELECT d.id, d.device_code, d.name, d.type, d.location, d.status, d.capacity_kw, d.owner_id, d.installed_on, u.name AS owner_name " +
                     "FROM devices d LEFT JOIN users u ON d.owner_id = u.id WHERE d.type = ? ORDER BY d.id ASC";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToDevice(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding devices by type: {}", type, e);
            throw new DataAccessException("Failed to query devices by type", e);
        }
        return list;
    }

    @Override
    public boolean create(EnergyDevice device) {
        String sql = "INSERT INTO devices (device_code, name, type, location, status, capacity_kw, owner_id, installed_on) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, device.getDeviceCode().trim().toUpperCase());
            ps.setString(2, device.getName().trim());
            ps.setString(3, device.getType().name());
            ps.setString(4, device.getLocation().trim());
            ps.setString(5, device.getStatus().name());
            ps.setDouble(6, device.getCapacityKw() != null ? device.getCapacityKw() : 5.0);
            if (device.getOwnerId() != null && device.getOwnerId() > 0) {
                ps.setLong(7, device.getOwnerId());
            } else {
                ps.setNull(7, Types.BIGINT);
            }
            LocalDate installed = device.getInstalledOn() != null ? device.getInstalledOn() : LocalDate.now();
            ps.setDate(8, Date.valueOf(installed));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet gk = ps.getGeneratedKeys()) {
                    if (gk.next()) {
                        device.setId(gk.getLong(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            logger.error("Error creating device: {}", device.getDeviceCode(), e);
            throw new DataAccessException("Failed to persist device", e);
        }
    }

    @Override
    public boolean update(EnergyDevice device) {
        String sql = "UPDATE devices SET name = ?, type = ?, location = ?, status = ?, capacity_kw = ?, owner_id = ?, installed_on = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, device.getName().trim());
            ps.setString(2, device.getType().name());
            ps.setString(3, device.getLocation().trim());
            ps.setString(4, device.getStatus().name());
            ps.setDouble(5, device.getCapacityKw() != null ? device.getCapacityKw() : 5.0);
            if (device.getOwnerId() != null && device.getOwnerId() > 0) {
                ps.setLong(6, device.getOwnerId());
            } else {
                ps.setNull(6, Types.BIGINT);
            }
            ps.setDate(7, Date.valueOf(device.getInstalledOn() != null ? device.getInstalledOn() : LocalDate.now()));
            ps.setLong(8, device.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating device ID: {}", device.getId(), e);
            throw new DataAccessException("Failed to update device details", e);
        }
    }

    @Override
    public boolean updateStatus(Long deviceId, DeviceStatus status) {
        String sql = "UPDATE devices SET status = ? WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setLong(2, deviceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error updating device status: ID {} -> {}", deviceId, status, e);
            throw new DataAccessException("Failed to update device status", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM devices WHERE id = ?";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error deleting device ID: {}", id, e);
            throw new DataAccessException("Failed to delete device", e);
        }
    }

    @Override
    public long countActiveDevices() {
        String sql = "SELECT count(*) FROM devices WHERE status = 'ONLINE'";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            logger.error("Error counting active devices", e);
            throw new DataAccessException("Failed to count active devices", e);
        }
        return 0;
    }

    @Override
    public long countTotalDevices() {
        String sql = "SELECT count(*) FROM devices";
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            logger.error("Error counting total devices", e);
            throw new DataAccessException("Failed to count total devices", e);
        }
        return 0;
    }

    private EnergyDevice mapResultSetToDevice(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String code = rs.getString("device_code");
        String name = rs.getString("name");
        DeviceType type = DeviceType.fromString(rs.getString("type"));
        String location = rs.getString("location");
        DeviceStatus status = DeviceStatus.fromString(rs.getString("status"));
        Double capacityKw = rs.getDouble("capacity_kw");
        Long ownerId = rs.getLong("owner_id");
        if (rs.wasNull()) ownerId = null;
        Date d = rs.getDate("installed_on");
        LocalDate installedOn = d != null ? d.toLocalDate() : LocalDate.now();

        // Polymorphic instantiator
        EnergyDevice device = Device.createPolymorphicDevice(id, code, name, type, location, status, capacityKw, ownerId, installedOn);
        device.setOwnerName(rs.getString("owner_name"));
        return device;
    }
}
