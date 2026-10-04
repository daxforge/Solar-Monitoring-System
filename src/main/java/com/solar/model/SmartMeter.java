package com.solar.model;

import com.solar.enums.DeviceStatus;
import com.solar.enums.DeviceType;

import java.time.LocalDate;

/**
 * Concrete model for Bidirectional Smart Energy Meters.
 * Measures grid feed-in and residential draw.
 */
public class SmartMeter extends EnergyDevice {
    private static final long serialVersionUID = 1L;

    private boolean isBidirectional;
    private double samplingRateHz;

    public SmartMeter() {
        super();
        setType(DeviceType.SMART_METER);
        this.isBidirectional = true;
        this.samplingRateHz = 1.0;
    }

    public SmartMeter(Long id, String deviceCode, String name, String location,
                      DeviceStatus status, Double capacityKw, Long ownerId, LocalDate installedOn) {
        super(id, deviceCode, name, DeviceType.SMART_METER, location, status, capacityKw, ownerId, installedOn);
        this.isBidirectional = true;
        this.samplingRateHz = 1.0;
    }

    @Override
    public String getTelemetrySummary() {
        return String.format("Smart Meter: Bi-directional grid interface sampling at %.1f Hz", samplingRateHz);
    }

    @Override
    public double calculateEfficiencyFactor() {
        // Meters have virtually no parasitic loss (>99.5% accuracy)
        return getStatus() == DeviceStatus.ONLINE ? 0.995 : 0.0;
    }

    public boolean isBidirectional() {
        return isBidirectional;
    }

    public void setBidirectional(boolean bidirectional) {
        isBidirectional = bidirectional;
    }

    public double getSamplingRateHz() {
        return samplingRateHz;
    }

    public void setSamplingRateHz(double samplingRateHz) {
        this.samplingRateHz = samplingRateHz;
    }
}
