package com.solar.util;

import com.solar.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Robust input validation utility class.
 * Ensures data integrity across services and controllers, guarding against malformed inputs.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern DEVICE_CODE_PATTERN = Pattern.compile(
            "^[A-Z0-9_-]{3,30}$"
    );

    private ValidationUtil() {
        // Enforce non-instantiability
    }

    /**
     * Validates that a string is neither null nor blank.
     */
    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required and cannot be blank.");
        }
    }

    /**
     * Validates that an email has valid syntax.
     */
    public static void validateEmail(String email) {
        requireNonBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email format: " + email);
        }
    }

    /**
     * Validates password strength (minimum 6 characters).
     */
    public static void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long.");
        }
    }

    /**
     * Validates device code naming format.
     */
    public static void validateDeviceCode(String deviceCode) {
        requireNonBlank(deviceCode, "Device Code");
        if (!DEVICE_CODE_PATTERN.matcher(deviceCode.trim().toUpperCase()).matches()) {
            throw new ValidationException("Device code must be 3-30 characters (letters, numbers, hyphens).");
        }
    }

    /**
     * Validates positive capacity metric.
     */
    public static void validatePositiveNumber(Double value, String fieldName) {
        if (value == null || value <= 0.0) {
            throw new ValidationException(fieldName + " must be a positive number greater than 0.");
        }
    }

    /**
     * Validates range percentage (0.0 to 100.0).
     */
    public static void validatePercentage(Double value, String fieldName) {
        if (value == null || value < 0.0 || value > 100.0) {
            throw new ValidationException(fieldName + " must be between 0% and 100%.");
        }
    }
}
