package com.solar.model;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;

import java.time.LocalDate;

/**
 * Concrete model for Chemical Battery Energy Storage Systems (BESS).
 * Extends {@link EnergyDevice} and encapsulates state-of-charge parameters.
 */
public class Battery extends EnergyDevice {
    private static final long serialVersionUID = 1L;

    private double totalStorageKwh; // Storage capacity in kWh
    private double currentSocPercent; // State of Charge (0 - 100%)
    private double depthOfDischargeMax; // DoD limit (e.g. 90%)

    public Battery() {
        super();
        setType(DeviceType.BATTERY);
        this.totalStorageKwh = 13.5;
        this.currentSocPercent = 80.0;
        this.depthOfDischargeMax = 90.0;
    }

    public Battery(Long id, String deviceCode, String name, String location,
                   DeviceStatus status, Double capacityKw, Long ownerId, LocalDate installedOn) {
        super(id, deviceCode, name, DeviceType.BATTERY, location, status, capacityKw, ownerId, installedOn);
        this.totalStorageKwh = capacityKw != null ? capacityKw * 2.5 : 13.5;
        this.currentSocPercent = 80.0;
        this.depthOfDischargeMax = 90.0;
    }

    @Override
    public String getTelemetrySummary() {
        return String.format("Battery Storage: %.1f kWh capacity, current charge: %.1f%%",
                totalStorageKwh, currentSocPercent);
    }

    @Override
    public double calculateEfficiencyFactor() {
        // Round-trip AC/DC chemical storage efficiency is typically ~92%
        if (getStatus() != DeviceStatus.ONLINE) return 0.0;
        return 0.92;
    }

    /**
     * Estimates remaining autonomy runtime in hours given an active household load in kW.
     * @param continuousLoadKw active consumption rate
     * @return estimated hours until reserve reached
     */
    public double estimateRemainingHours(double continuousLoadKw) {
        if (continuousLoadKw <= 0.05) return 24.0;
        double usableEnergyKwh = totalStorageKwh * (currentSocPercent / 100.0) * (depthOfDischargeMax / 100.0);
        return Math.max(0.0, usableEnergyKwh / continuousLoadKw);
    }

    public double getTotalStorageKwh() {
        return totalStorageKwh;
    }

    public void setTotalStorageKwh(double totalStorageKwh) {
        this.totalStorageKwh = totalStorageKwh;
    }

    public double getCurrentSocPercent() {
        return currentSocPercent;
    }

    public void setCurrentSocPercent(double currentSocPercent) {
        this.currentSocPercent = currentSocPercent;
    }

    public double getDepthOfDischargeMax() {
        return depthOfDischargeMax;
    }

    public void setDepthOfDischargeMax(double depthOfDischargeMax) {
        this.depthOfDischargeMax = depthOfDischargeMax;
    }
}
