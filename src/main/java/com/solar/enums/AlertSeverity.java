package com.solar.enums;

/**
 * Alert severity levels for system events and anomalies.
 */
public enum AlertSeverity {
    INFO("Information", "severity-info"),
    WARNING("Warning", "severity-warning"),
    CRITICAL("Critical", "severity-critical");

    private final String label;
    private final String badgeClass;

    AlertSeverity(String label, String badgeClass) {
        this.label = label;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getBadgeClass() {
        return badgeClass;
    }

    public static AlertSeverity fromString(String text) {
        if (text == null) return INFO;
        for (AlertSeverity s : AlertSeverity.values()) {
            if (s.name().equalsIgnoreCase(text.trim())) {
                return s;
            }
        }
        return INFO;
    }
}
