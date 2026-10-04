package com.solar.model;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;

import java.time.LocalDate;

/**
 * Concrete device entity representation for general DAO persistence and polymorphic dispatch.
 */
public class Device extends EnergyDevice {
    private static final long serialVersionUID = 1L;

    public Device() {
        super();
    }

    public Device(Long id, String deviceCode, String name, DeviceType type,
                  String location, DeviceStatus status, Double capacityKw,
                  Long ownerId, LocalDate installedOn) {
        super(id, deviceCode, name, type, location, status, capacityKw, ownerId, installedOn);
    }

    @Override
    public String getTelemetrySummary() {
        if (getType() != null) {
            return getType().getDisplayName() + " [" + getDeviceCode() + "] @ " + getLocation();
        }
        return "Device [" + getDeviceCode() + "] @ " + getLocation();
    }

    @Override
    public double calculateEfficiencyFactor() {
        if (getStatus() != DeviceStatus.ONLINE) return 0.0;
        return 0.95;
    }

    /**
     * Polymorphic Factory Pattern to produce specialized subclass instances.
     */
    public static EnergyDevice createPolymorphicDevice(Long id, String deviceCode, String name,
                                                       DeviceType type, String location,
                                                       DeviceStatus status, Double capacityKw,
                                                       Long ownerId, LocalDate installedOn) {
        if (type == null) {
            return new Device(id, deviceCode, name, DeviceType.SOLAR_PANEL, location, status, capacityKw, ownerId, installedOn);
        }
        return switch (type) {
            case SOLAR_PANEL -> new SolarPanel(id, deviceCode, name, location, status, capacityKw, ownerId, installedOn);
            case BATTERY -> new Battery(id, deviceCode, name, location, status, capacityKw, ownerId, installedOn);
            case INVERTER -> new Inverter(id, deviceCode, name, location, status, capacityKw, ownerId, installedOn);
            case SMART_METER -> new SmartMeter(id, deviceCode, name, location, status, capacityKw, ownerId, installedOn);
        };
    }
}
