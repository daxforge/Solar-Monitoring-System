package com.solar.enums;

/**
 * Device operational statuses representing physical and network health.
 */
public enum DeviceStatus {
    ONLINE("Online", "Operational and sending telemetry", "status-online"),
    OFFLINE("Offline", "Telemetry heartbeat missed", "status-offline"),
    MAINTENANCE("Maintenance", "Under inspection or scheduled service", "status-maintenance");

    private final String label;
    private final String description;
    private final String cssClass;

    DeviceStatus(String label, String description, String cssClass) {
        this.label = label;
        this.description = description;
        this.cssClass = cssClass;
    }

    public String getLabel() {
        return label;
    }

    public String getDescription() {
        return description;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static DeviceStatus fromString(String text) {
        if (text == null) return OFFLINE;
        for (DeviceStatus s : DeviceStatus.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return OFFLINE;
    }
}
