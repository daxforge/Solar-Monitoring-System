package com.solar.enums;

/**
 * Categorization of solar energy system hardware.
 */
public enum DeviceType {
    SOLAR_PANEL("Solar Photovoltaic Array", "Harvests solar irradiance into DC electricity"),
    BATTERY("Energy Storage Battery", "Stores excess solar energy for nighttime and peak hours"),
    INVERTER("Power Inverter", "Converts direct current (DC) to alternating current (AC)"),
    SMART_METER("Smart Energy Meter", "Measures grid import, export and household load");

    private final String displayName;
    private final String functionDescription;

    DeviceType(String displayName, String functionDescription) {
        this.displayName = displayName;
        this.functionDescription = functionDescription;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFunctionDescription() {
        return functionDescription;
    }

    public static DeviceType fromString(String text) {
        if (text == null) return SOLAR_PANEL;
        for (DeviceType t : DeviceType.values()) {
            if (t.name().equalsIgnoreCase(text.trim())) {
                return t;
            }
        }
        return SOLAR_PANEL;
    }
}
