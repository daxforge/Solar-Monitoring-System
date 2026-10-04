package com.solar.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing real-time battery storage status and health metrics.
 */
public class StorageStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private Double chargePercent; // 0.0 - 100.0%
    private String state; // 'CHARGING', 'DISCHARGING', 'IDLE', 'STANDBY'
    private Double healthPercent; // State of Health (SoH)
    private LocalDateTime updatedAt;

    public StorageStatus() {
        this.chargePercent = 80.0;
        this.state = "STANDBY";
        this.healthPercent = 98.0;
        this.updatedAt = LocalDateTime.now();
    }

    public StorageStatus(Long id, Long deviceId, Double chargePercent,
                         String state, Double healthPercent, LocalDateTime updatedAt) {
        this.id = id;
        this.deviceId = deviceId;
        this.chargePercent = chargePercent != null ? chargePercent : 80.0;
        this.state = state != null ? state : "STANDBY";
        this.healthPercent = healthPercent != null ? healthPercent : 98.0;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    /**
     * Estimated time to empty in hours based on current load (default 1.5 kW load).
     */
    public double getEstimatedTimeToEmptyHours() {
        if ("CHARGING".equalsIgnoreCase(state)) return 0.0; // Battery is replenishing
        double remainingCapacityKwh = 13.5 * (chargePercent / 100.0);
        return Math.max(0.1, remainingCapacityKwh / 1.5);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Double getChargePercent() {
        return chargePercent;
    }

    public void setChargePercent(Double chargePercent) {
        this.chargePercent = chargePercent;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getHealthPercent() {
        return healthPercent;
    }

    public void setHealthPercent(Double healthPercent) {
        this.healthPercent = healthPercent;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "StorageStatus{" +
                "deviceId=" + deviceId +
                ", charge=" + chargePercent + "%" +
                ", state='" + state + '\'' +
                ", health=" + healthPercent + "%" +
                '}';
    }
}
