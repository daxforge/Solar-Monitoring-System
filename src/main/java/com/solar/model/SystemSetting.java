package com.solar.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing global system settings and threshold parameters.
 */
public class SystemSetting implements Serializable {
    private static final long serialVersionUID = 1L;

    private String settingKey;
    private String settingValue;
    private Long updatedBy;
    private String updatedByName;
    private LocalDateTime updatedAt;

    public SystemSetting() {
        this.updatedAt = LocalDateTime.now();
    }

    public SystemSetting(String settingKey, String settingValue, Long updatedBy, LocalDateTime updatedAt) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.updatedBy = updatedBy;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedByName() {
        return updatedByName;
    }

    public void setUpdatedByName(String updatedByName) {
        this.updatedByName = updatedByName;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "SystemSetting{" +
                "key='" + settingKey + '\'' +
                ", value='" + settingValue + '\'' +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
