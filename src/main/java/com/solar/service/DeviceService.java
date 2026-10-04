package com.solar.service;

import com.solar.dao.DeviceDao;
import com.solar.dao.impl.DeviceDaoImpl;
import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;
import com.solar.exception.DeviceNotFoundException;
import com.solar.exception.ValidationException;
import com.solar.model.Device;
import com.solar.model.EnergyDevice;
import com.solar.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service orchestrating energy hardware registration, assignments and status management.
 */
public class DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);

    private final DeviceDao deviceDao;

    public DeviceService() {
        this.deviceDao = new DeviceDaoImpl();
    }

    public DeviceService(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public List<EnergyDevice> getAllDevices() {
        return deviceDao.findAll();
    }

    public List<EnergyDevice> getDevicesByOwner(Long ownerId) {
        return deviceDao.findByOwnerId(ownerId);
    }

    public EnergyDevice getDeviceById(Long id) {
        return deviceDao.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    public Optional<EnergyDevice> findByCode(String code) {
        if (code == null) return Optional.empty();
        return deviceDao.findByCode(code.trim().toUpperCase());
    }

    /**
     * Registers a new physical or virtual energy device.
     */
    public EnergyDevice registerDevice(String code, String name, DeviceType type,
                                       String location, DeviceStatus status,
                                       Double capacityKw, Long ownerId, LocalDate installedOn) {
        ValidationUtil.validateDeviceCode(code);
        ValidationUtil.requireNonBlank(name, "Device Name");
        ValidationUtil.requireNonBlank(location, "Location");
        ValidationUtil.validatePositiveNumber(capacityKw, "Capacity (kW)");

        String normalizedCode = code.trim().toUpperCase();
        if (deviceDao.findByCode(normalizedCode).isPresent()) {
            throw new ValidationException("Device code '" + normalizedCode + "' is already registered.");
        }

        EnergyDevice device = Device.createPolymorphicDevice(
                null,
                normalizedCode,
                name.trim(),
                type != null ? type : DeviceType.SOLAR_PANEL,
                location.trim(),
                status != null ? status : DeviceStatus.ONLINE,
                capacityKw,
                ownerId,
                installedOn != null ? installedOn : LocalDate.now()
        );

        deviceDao.create(device);
        logger.info("New device registered: {} [{}]", device.getName(), device.getDeviceCode());
        return device;
    }

    /**
     * Updates device specifications and assignments.
     */
    public void updateDevice(Long id, String name, DeviceType type, String location,
                             DeviceStatus status, Double capacityKw, Long ownerId, LocalDate installedOn) {
        ValidationUtil.requireNonBlank(name, "Device Name");
        ValidationUtil.requireNonBlank(location, "Location");
        ValidationUtil.validatePositiveNumber(capacityKw, "Capacity (kW)");

        EnergyDevice existing = getDeviceById(id);
        existing.setName(name.trim());
        if (type != null) existing.setType(type);
        existing.setLocation(location.trim());
        if (status != null) existing.setStatus(status);
        existing.setCapacityKw(capacityKw);
        existing.setOwnerId(ownerId);
        if (installedOn != null) existing.setInstalledOn(installedOn);

        deviceDao.update(existing);
        logger.info("Device ID {} updated successfully.", id);
    }

    public void updateDeviceStatus(Long id, DeviceStatus status) {
        getDeviceById(id);
        deviceDao.updateStatus(id, status);
        logger.info("Device ID {} status updated to {}", id, status);
    }

    public void deleteDevice(Long id) {
        getDeviceById(id);
        deviceDao.delete(id);
        logger.info("Device ID {} removed from system.", id);
    }

    public long getActiveDeviceCount() {
        return deviceDao.countActiveDevices();
    }

    public long getTotalDeviceCount() {
        return deviceDao.countTotalDevices();
    }
}
