package com.solar.exception;

/**
 * Thrown when an energy device cannot be resolved by its ID or device code.
 */
public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message) {
        super(message);
    }

    public DeviceNotFoundException(Long deviceId) {
        super("Energy device not found with ID: " + deviceId);
    }
}
