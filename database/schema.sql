-- ============================================================================
-- Solar Energy Monitoring System
-- Database Schema Definition (MySQL 8.0+ / H2 Compatible DDL)
-- ============================================================================

CREATE DATABASE IF NOT EXISTS solar_db;
USE solar_db;

-- 1. Users Table
-- Stores system administrators and residential homeowners
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN', 'HOMEOWNER'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE,
    INDEX idx_user_email (email),
    INDEX idx_user_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Devices Table
-- Physical hardware registered: solar panels, batteries, inverters, smart meters
CREATE TABLE IF NOT EXISTS devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(30) NOT NULL, -- 'SOLAR_PANEL', 'BATTERY', 'INVERTER', 'SMART_METER'
    location VARCHAR(150) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ONLINE', -- 'ONLINE', 'OFFLINE', 'MAINTENANCE'
    capacity_kw DECIMAL(8,2) NOT NULL DEFAULT 5.00,
    owner_id BIGINT NULL,
    installed_on DATE NOT NULL,
    FOREIGN KEY (owner_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_device_owner (owner_id),
    INDEX idx_device_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Energy Readings Table
-- High-frequency time-series telemetry records
CREATE TABLE IF NOT EXISTS energy_readings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL,
    reading_time TIMESTAMP NOT NULL,
    production_kw DECIMAL(8,3) NOT NULL DEFAULT 0.000,
    consumption_kw DECIMAL(8,3) NOT NULL DEFAULT 0.000,
    battery_percent DECIMAL(5,2) NOT NULL DEFAULT 0.00,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE,
    INDEX idx_device_time (device_id, reading_time),
    INDEX idx_reading_time (reading_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Storage Status Table
-- Detailed diagnostic state for battery storage units
CREATE TABLE IF NOT EXISTS storage_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    device_id BIGINT NOT NULL UNIQUE,
    charge_percent DECIMAL(5,2) NOT NULL,
    state VARCHAR(30) NOT NULL, -- 'CHARGING', 'DISCHARGING', 'IDLE', 'STANDBY'
    health_percent DECIMAL(5,2) NOT NULL DEFAULT 100.00,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Alerts Table
-- Event logs, safety warnings and system threshold violations
CREATE TABLE IF NOT EXISTS alerts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_id BIGINT NULL,
    severity VARCHAR(20) NOT NULL, -- 'INFO', 'WARNING', 'CRITICAL'
    message VARCHAR(255) NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (device_id) REFERENCES devices(id) ON DELETE SET NULL,
    INDEX idx_user_unread (user_id, is_read),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. System Settings Table
-- Global thresholds and operational configurations
CREATE TABLE IF NOT EXISTS system_settings (
    setting_key VARCHAR(50) PRIMARY KEY,
    setting_value VARCHAR(255) NOT NULL,
    updated_by BIGINT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (updated_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
