package com.solar.model;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;

import java.time.LocalDate;

/**
 * Concrete model for Solar Hybrid and String Inverters.
 * Handles DC-to-AC conversion telemetry and peak load thresholds.
 */
public class Inverter extends EnergyDevice {
    private static final long serialVersionUID = 1L;

    private double conversionEfficiencyPercent; // e.g. 97.5%
    private int mpptChannels; // Maximum Power Point Tracking channels

    public Inverter() {
        super();
        setType(DeviceType.INVERTER);
        this.conversionEfficiencyPercent = 97.5;
        this.mpptChannels = 2;
    }

    public Inverter(Long id, String deviceCode, String name, String location,
                    DeviceStatus status, Double capacityKw, Long ownerId, LocalDate installedOn) {
        super(id, deviceCode, name, DeviceType.INVERTER, location, status, capacityKw, ownerId, installedOn);
        this.conversionEfficiencyPercent = 97.5;
        this.mpptChannels = 2;
    }

    @Override
    public String getTelemetrySummary() {
        return String.format("Hybrid Inverter: %.1f kW rating, %d MPPT trackers, %.1f%% efficiency",
                getNominalCapacityKw(), mpptChannels, conversionEfficiencyPercent);
    }

    @Override
    public double calculateEfficiencyFactor() {
        if (getStatus() != DeviceStatus.ONLINE) return 0.0;
        return conversionEfficiencyPercent / 100.0;
    }

    public double getConversionEfficiencyPercent() {
        return conversionEfficiencyPercent;
    }

    public void setConversionEfficiencyPercent(double conversionEfficiencyPercent) {
        this.conversionEfficiencyPercent = conversionEfficiencyPercent;
    }

    public int getMpptChannels() {
        return mpptChannels;
    }

    public void setMpptChannels(int mpptChannels) {
        this.mpptChannels = mpptChannels;
    }
}
