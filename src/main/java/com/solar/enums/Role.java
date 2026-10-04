package com.solar.enums;

/**
 * Role enumeration defining application authorization levels.
 * Maps to access control policies enforced in {@link com.solar.filter.RoleFilter}.
 */
public enum Role {
    ADMIN("Administrator", "Full system governance and configuration access"),
    HOMEOWNER("Homeowner", "Personal solar production, consumption and storage visibility");

    private final String displayName;
    private final String description;

    Role(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static Role fromString(String text) {
        if (text == null) return HOMEOWNER;
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(text.trim())) {
                return r;
            }
        }
        return HOMEOWNER;
    }
}
