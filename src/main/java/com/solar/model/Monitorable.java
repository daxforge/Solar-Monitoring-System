package com.solar.model;

import com.solar.enums.DeviceStatus;

/**
 * Interface defining monitoring and telemetry contracts for physical energy hardware.
 * Demonstrates abstraction and interface design in Core Java.
 */
public interface Monitorable {

    /**
     * Retrieves the primary rated or operational output metric of the device in kW.
     * @return current or nominal capacity/output in kW
     */
    double getNominalCapacityKw();

    /**
     * Returns an executive diagnostic summary string for display in UI cards.
     * @return telemetry summary description
     */
    String getTelemetrySummary();

    /**
     * Calculates the estimated efficiency factor (0.0 to 1.0) under typical conditions.
     * @return double efficiency factor
     */
    double calculateEfficiencyFactor();

    /**
     * Checks if the device is operational and capable of streaming telemetry.
     * @return boolean true if ONLINE
     */
    default boolean isOperational(DeviceStatus status) {
        return status == DeviceStatus.ONLINE;
    }
}
