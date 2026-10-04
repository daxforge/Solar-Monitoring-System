package com.solar.model;

import com.solar.enums.AlertSeverity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Model representing system event logs, performance warnings, and safety alerts.
 */
public class Alert implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String userName;
    private Long deviceId;
    private String deviceCode;
    private String deviceName;
    private AlertSeverity severity;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public Alert() {
        this.severity = AlertSeverity.INFO;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }

    public Alert(Long id, Long userId, Long deviceId, AlertSeverity severity,
                 String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.deviceId = deviceId;
        this.severity = severity != null ? severity : AlertSeverity.INFO;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", userId=" + userId +
                ", severity=" + severity +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                '}';
    }
}
