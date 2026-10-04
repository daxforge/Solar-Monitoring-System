package com.solar.dao;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.model.EnergyDevice;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Energy Hardware Devices.
 */
public interface DeviceDao {
    Optional<EnergyDevice> findById(Long id);
    Optional<EnergyDevice> findByCode(String deviceCode);
    List<EnergyDevice> findAll();
    List<EnergyDevice> findByOwnerId(Long ownerId);
    List<EnergyDevice> findByType(DeviceType type);
    boolean create(EnergyDevice device);
    boolean update(EnergyDevice device);
    boolean updateStatus(Long deviceId, DeviceStatus status);
    boolean delete(Long id);
    long countActiveDevices();
    long countTotalDevices();
}
