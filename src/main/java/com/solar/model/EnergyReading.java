package com.solar.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing a granular energy reading telemetry snapshot.
 */
public class EnergyReading implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private LocalDateTime readingTime;
    private Double productionKw;
    private Double consumptionKw;
    private Double batteryPercent;

    public EnergyReading() {
        this.readingTime = LocalDateTime.now();
        this.productionKw = 0.0;
        this.consumptionKw = 0.0;
        this.batteryPercent = 0.0;
    }

    public EnergyReading(Long id, Long deviceId, LocalDateTime readingTime,
                         Double productionKw, Double consumptionKw, Double batteryPercent) {
        this.id = id;
        this.deviceId = deviceId;
        this.readingTime = readingTime != null ? readingTime : LocalDateTime.now();
        this.productionKw = productionKw != null ? productionKw : 0.0;
        this.consumptionKw = consumptionKw != null ? consumptionKw : 0.0;
        this.batteryPercent = batteryPercent != null ? batteryPercent : 0.0;
    }

    /**
     * Net energy surplus (positive) or deficit (negative) in kW.
     */
    public Double getNetPowerKw() {
        return (productionKw != null ? productionKw : 0.0) - (consumptionKw != null ? consumptionKw : 0.0);
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

    public LocalDateTime getReadingTime() {
        return readingTime;
    }

    public void setReadingTime(LocalDateTime readingTime) {
        this.readingTime = readingTime;
    }

    public Double getProductionKw() {
        return productionKw;
    }

    public void setProductionKw(Double productionKw) {
        this.productionKw = productionKw;
    }

    public Double getConsumptionKw() {
        return consumptionKw;
    }

    public void setConsumptionKw(Double consumptionKw) {
        this.consumptionKw = consumptionKw;
    }

    public Double getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(Double batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    @Override
    public String toString() {
        return "EnergyReading{" +
                "id=" + id +
                ", deviceId=" + deviceId +
                ", time=" + readingTime +
                ", prod=" + productionKw + "kW" +
                ", cons=" + consumptionKw + "kW" +
                ", batt=" + batteryPercent + "%" +
                '}';
    }
}
