package com.solar.model;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;

import java.time.LocalDate;

/**
 * Concrete model for Photovoltaic Solar Arrays.
 * Inherits from {@link EnergyDevice}, illustrating OOP inheritance and method overriding.
 */
public class SolarPanel extends EnergyDevice {
    private static final long serialVersionUID = 1L;

    private int panelCount;
    private double efficiencyRatingPercent; // e.g. 21.5%

    public SolarPanel() {
        super();
        setType(DeviceType.SOLAR_PANEL);
        this.efficiencyRatingPercent = 21.5;
        this.panelCount = 16;
    }

    public SolarPanel(Long id, String deviceCode, String name, String location,
                      DeviceStatus status, Double capacityKw, Long ownerId, LocalDate installedOn) {
        super(id, deviceCode, name, DeviceType.SOLAR_PANEL, location, status, capacityKw, ownerId, installedOn);
        this.efficiencyRatingPercent = 21.5;
        this.panelCount = 16;
    }

    @Override
    public String getTelemetrySummary() {
        return String.format("Solar Array: %d monocrystalline cells rated at %.1f kW peak capacity",
                panelCount, getNominalCapacityKw());
    }

    @Override
    public double calculateEfficiencyFactor() {
        // High efficiency degradation calculation based on status
        if (getStatus() == DeviceStatus.OFFLINE) return 0.0;
        if (getStatus() == DeviceStatus.MAINTENANCE) return 0.45;
        return (efficiencyRatingPercent / 100.0);
    }

    public int getPanelCount() {
        return panelCount;
    }

    public void setPanelCount(int panelCount) {
        this.panelCount = panelCount;
    }

    public double getEfficiencyRatingPercent() {
        return efficiencyRatingPercent;
    }

    public void setEfficiencyRatingPercent(double efficiencyRatingPercent) {
        this.efficiencyRatingPercent = efficiencyRatingPercent;
    }
}
