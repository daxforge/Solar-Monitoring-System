package com.solar.util;

/**
 * Global application constants: session attributes, setting keys, and route definitions.
 */
public final class Constants {

    private Constants() {
        // Enforce non-instantiability
    }

    // Session Attribute Keys
    public static final String SESSION_USER = "currentUser";
    public static final String SESSION_CSRF_TOKEN = "csrfToken";
    public static final String FLASH_SUCCESS = "flashSuccess";
    public static final String FLASH_ERROR = "flashError";

    // Global System Setting Keys
    public static final String SETTING_SYSTEM_NAME = "system_name";
    public static final String SETTING_TARIFF_PER_KWH = "tariff_per_kwh";
    public static final String SETTING_LOW_BATTERY_THRESHOLD = "low_battery_threshold";
    public static final String SETTING_LOW_PRODUCTION_THRESHOLD = "low_production_threshold";
    public static final String SETTING_HIGH_CONSUMPTION_THRESHOLD = "high_consumption_threshold";
    public static final String SETTING_REFRESH_INTERVAL_SEC = "refresh_interval_sec";

    // Default Configuration Values
    public static final String DEFAULT_SYSTEM_NAME = "Helios Solar Energy Monitoring";
    public static final double DEFAULT_TARIFF_PER_KWH = 0.18;
    public static final double DEFAULT_LOW_BATTERY_THRESHOLD = 20.0; // percent
    public static final double DEFAULT_LOW_PRODUCTION_THRESHOLD = 30.0; // percent drop
    public static final double DEFAULT_HIGH_CONSUMPTION_THRESHOLD = 7.5; // kW
    public static final int DEFAULT_REFRESH_INTERVAL_SEC = 5;

    // Route Mappings
    public static final String URL_LOGIN = "/login";
    public static final String URL_LOGOUT = "/logout";
    public static final String URL_ADMIN_DASHBOARD = "/admin/dashboard";
    public static final String URL_HOMEOWNER_DASHBOARD = "/homeowner/dashboard";
    public static final String URL_PROFILE = "/profile";
    public static final String URL_ALERTS = "/alerts";
    public static final String URL_API_TELEMETRY = "/api/telemetry";
}
