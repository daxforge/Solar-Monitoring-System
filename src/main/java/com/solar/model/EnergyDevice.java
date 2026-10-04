package com.solar.model;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Abstract base class representing any hardware unit in the solar monitoring ecosystem.
 * Demonstrates OOP principles: Encapsulation, Abstraction, and Polymorphism.
 */
public abstract class EnergyDevice implements Monitorable, Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String deviceCode;
    private String name;
    private DeviceType type;
    private String location;
    private DeviceStatus status;
    private Double capacityKw;
    private Long ownerId;
    private String ownerName; // Joined view property
    private LocalDate installedOn;

    public EnergyDevice() {
        this.status = DeviceStatus.ONLINE;
        this.installedOn = LocalDate.now();
    }

    public EnergyDevice(Long id, String deviceCode, String name, DeviceType type,
                        String location, DeviceStatus status, Double capacityKw,
                        Long ownerId, LocalDate installedOn) {
        this.id = id;
        this.deviceCode = deviceCode;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = status != null ? status : DeviceStatus.ONLINE;
        this.capacityKw = capacityKw != null ? capacityKw : 5.0;
        this.ownerId = ownerId;
        this.installedOn = installedOn != null ? installedOn : LocalDate.now();
    }

    // Abstract methods enforced on all specialized device subclasses
    @Override
    public abstract String getTelemetrySummary();

    @Override
    public abstract double calculateEfficiencyFactor();

    @Override
    public double getNominalCapacityKw() {
        return capacityKw != null ? capacityKw : 0.0;
    }

    // Encapsulation: standard getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DeviceStatus getStatus() {
        return status;
    }

    public void setStatus(DeviceStatus status) {
        this.status = status;
    }

    public Double getCapacityKw() {
        return capacityKw;
    }

    public void setCapacityKw(Double capacityKw) {
        this.capacityKw = capacityKw;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public LocalDate getInstalledOn() {
        return installedOn;
    }

    public void setInstalledOn(LocalDate installedOn) {
        this.installedOn = installedOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnergyDevice that = (EnergyDevice) o;
        return Objects.equals(id, that.id) || Objects.equals(deviceCode, that.deviceCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, deviceCode);
    }

    @Override
    public String toString() {
        return "EnergyDevice{" +
                "id=" + id +
                ", code='" + deviceCode + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", capacity=" + capacityKw + "kW" +
                '}';
    }
}
