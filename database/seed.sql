-- ============================================================================
-- Solar Energy Monitoring System
-- Database Seed Data (Sample Dataset for Testing & Evaluation)
-- Default Passwords:
-- Admin:      admin@solar.local   / admin123
-- Homeowners: alice@solar.local   / user123
--             bob@solar.local     / user123
--             charlie@solar.local / user123
-- ============================================================================

USE solar_db;

-- 1. Insert Initial System Settings
INSERT INTO system_settings (setting_key, setting_value, updated_at) VALUES
('system_name', 'Helios Intelligent Solar Monitor', NOW()),
('tariff_per_kwh', '0.18', NOW()),
('low_battery_threshold', '20.0', NOW()),
('low_production_threshold', '30.0', NOW()),
('high_consumption_threshold', '7.5', NOW()),
('refresh_interval_sec', '5', NOW())
ON DUPLICATE KEY UPDATE setting_value = VALUES(setting_value);

-- 2. Insert Users (BCrypt hashes for 'admin123' and 'user123')
-- admin123: $2a$12$Nq9v7e8PqXbA7ZkZl8W5leF5mK2r1.L0d6Z8q5X.a8U7wK3r5pGeq
-- user123:  $2a$12$R.7w5b9Pz8a1K3l2W0v8XeF9mK5r3.L2d8Z0q7X.c0U9wK5r7pHeu
INSERT INTO users (id, name, email, password_hash, role, created_at, active) VALUES
(1, 'Administrator', 'admin@solar.local', '$2a$12$Nq9v7e8PqXbA7ZkZl8W5leF5mK2r1.L0d6Z8q5X.a8U7wK3r5pGeq', 'ADMIN', NOW(), TRUE),
(2, 'Alice Walker', 'alice@solar.local', '$2a$12$R.7w5b9Pz8a1K3l2W0v8XeF9mK5r3.L2d8Z0q7X.c0U9wK5r7pHeu', 'HOMEOWNER', NOW(), TRUE),
(3, 'Bob Jenkins', 'bob@solar.local', '$2a$12$R.7w5b9Pz8a1K3l2W0v8XeF9mK5r3.L2d8Z0q7X.c0U9wK5r7pHeu', 'HOMEOWNER', NOW(), TRUE),
(4, 'Charlie Davis', 'charlie@solar.local', '$2a$12$R.7w5b9Pz8a1K3l2W0v8XeF9mK5r3.L2d8Z0q7X.c0U9wK5r7pHeu', 'HOMEOWNER', NOW(), TRUE)
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 3. Insert 10 Devices
INSERT INTO devices (id, device_code, name, type, location, status, capacity_kw, owner_id, installed_on) VALUES
(1, 'DEV-SP-001', 'Rooftop Solar Array A', 'SOLAR_PANEL', 'Main Villa Roof, South Wing', 'ONLINE', 6.50, 2, '2023-03-15'),
(2, 'DEV-BAT-001', 'PowerVault Lithium L1', 'BATTERY', 'Garage Energy Center', 'ONLINE', 13.50, 2, '2023-03-16'),
(3, 'DEV-INV-001', 'SunnyBoy Hybrid 6.0', 'INVERTER', 'Garage Energy Center', 'ONLINE', 6.00, 2, '2023-03-16'),
(4, 'DEV-SM-001', 'SmartGrid Bi-Directional', 'SMART_METER', 'Utility Ingress Panel', 'ONLINE', 10.00, 2, '2023-03-15'),
(5, 'DEV-SP-002', 'Pergola High-Yield Solar', 'SOLAR_PANEL', 'Garden Terrace Canopy', 'ONLINE', 4.80, 3, '2023-06-10'),
(6, 'DEV-BAT-002', 'EcoPack LFP 10k', 'BATTERY', 'Basement Storage Vault', 'ONLINE', 10.00, 3, '2023-06-11'),
(7, 'DEV-INV-002', 'SolarEdge HD-Wave 5kW', 'INVERTER', 'Basement Utility Room', 'MAINTENANCE', 5.00, 3, '2023-06-11'),
(8, 'DEV-SM-002', 'Sub-panel Smart Monitor', 'SMART_METER', 'Primary Breaker Box', 'ONLINE', 8.00, 3, '2023-06-10'),
(9, 'DEV-SP-003', 'Bifacial Solar Carport', 'SOLAR_PANEL', 'West Driveway Carport', 'ONLINE', 5.20, 4, '2023-09-01'),
(10, 'DEV-SP-004', 'Auxiliary SunTracker Array', 'SOLAR_PANEL', 'East Lawn Ground Mount', 'OFFLINE', 3.50, 4, '2023-09-02')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 4. Insert Storage Status for Battery Units
INSERT INTO storage_status (device_id, charge_percent, state, health_percent, updated_at) VALUES
(2, 78.50, 'CHARGING', 98.20, NOW()),
(6, 42.00, 'DISCHARGING', 95.80, NOW())
ON DUPLICATE KEY UPDATE charge_percent = VALUES(charge_percent), state = VALUES(state);

-- 5. Insert Sample Alerts
INSERT INTO alerts (user_id, device_id, severity, message, is_read, created_at) VALUES
(2, 2, 'INFO', 'PowerVault battery reached optimal float charge level.', TRUE, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 1, 'WARNING', 'Solar production dropped 35% compared to expected peak irradiance.', FALSE, DATE_SUB(NOW(), INTERVAL 4 HOUR)),
(3, 7, 'WARNING', 'Inverter scheduled maintenance cycle initiated.', FALSE, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 6, 'CRITICAL', 'EcoPack battery temperature exceeded 42°C threshold.', FALSE, DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(4, 10, 'CRITICAL', 'Auxiliary SunTracker Array connection timed out (Device Offline).', FALSE, DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(1, 10, 'CRITICAL', 'System-wide event: Device DEV-SP-004 reported OFFLINE status.', FALSE, DATE_SUB(NOW(), INTERVAL 5 HOUR));

-- 6. Insert Historical 7-Day Energy Readings for Devices
-- Alice's Solar Panel (DEV-SP-001 / id=1)
INSERT INTO energy_readings (device_id, reading_time, production_kw, consumption_kw, battery_percent)
SELECT 1, DATE_SUB(NOW(), INTERVAL 6 DAY), 4.850, 1.200, 65.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 5 DAY), 5.200, 1.450, 72.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 4 DAY), 3.900, 1.800, 58.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 5.800, 2.100, 80.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 2 DAY), 6.100, 1.600, 85.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 1 DAY), 5.400, 1.950, 75.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 12 HOUR), 4.200, 2.400, 70.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 6 HOUR), 5.750, 2.100, 78.00 UNION ALL
SELECT 1, DATE_SUB(NOW(), INTERVAL 2 HOUR), 4.900, 1.850, 79.50 UNION ALL
SELECT 1, NOW(), 4.650, 1.900, 78.50;

-- Alice's Battery (DEV-BAT-001 / id=2)
INSERT INTO energy_readings (device_id, reading_time, production_kw, consumption_kw, battery_percent)
SELECT 2, DATE_SUB(NOW(), INTERVAL 6 DAY), 0.000, 0.400, 65.00 UNION ALL
SELECT 2, DATE_SUB(NOW(), INTERVAL 5 DAY), 0.000, 0.500, 72.00 UNION ALL
SELECT 2, DATE_SUB(NOW(), INTERVAL 4 DAY), 0.000, 0.450, 58.00 UNION ALL
SELECT 2, DATE_SUB(NOW(), INTERVAL 3 DAY), 0.000, 0.380, 80.00 UNION ALL
SELECT 2, DATE_SUB(NOW(), INTERVAL 2 DAY), 0.000, 0.420, 85.00 UNION ALL
SELECT 2, DATE_SUB(NOW(), INTERVAL 1 DAY), 0.000, 0.400, 75.00 UNION ALL
SELECT 2, DATE_SUB(NOW(), INTERVAL 2 HOUR), 0.000, 0.350, 78.00 UNION ALL
SELECT 2, NOW(), 0.000, 0.320, 78.50;

-- Bob's Solar Panel (DEV-SP-002 / id=5)
INSERT INTO energy_readings (device_id, reading_time, production_kw, consumption_kw, battery_percent)
SELECT 5, DATE_SUB(NOW(), INTERVAL 6 DAY), 3.900, 2.100, 50.00 UNION ALL
SELECT 5, DATE_SUB(NOW(), INTERVAL 5 DAY), 4.100, 2.300, 55.00 UNION ALL
SELECT 5, DATE_SUB(NOW(), INTERVAL 4 DAY), 2.800, 2.600, 40.00 UNION ALL
SELECT 5, DATE_SUB(NOW(), INTERVAL 3 DAY), 4.400, 1.900, 60.00 UNION ALL
SELECT 5, DATE_SUB(NOW(), INTERVAL 2 DAY), 4.600, 2.200, 62.00 UNION ALL
SELECT 5, DATE_SUB(NOW(), INTERVAL 1 DAY), 3.950, 2.450, 48.00 UNION ALL
SELECT 5, NOW(), 3.800, 2.300, 42.00;

-- Charlie's Solar Panel (DEV-SP-003 / id=9)
INSERT INTO energy_readings (device_id, reading_time, production_kw, consumption_kw, battery_percent)
SELECT 9, DATE_SUB(NOW(), INTERVAL 6 DAY), 4.100, 1.500, 60.00 UNION ALL
SELECT 9, DATE_SUB(NOW(), INTERVAL 5 DAY), 4.300, 1.600, 65.00 UNION ALL
SELECT 9, DATE_SUB(NOW(), INTERVAL 4 DAY), 3.200, 1.900, 52.00 UNION ALL
SELECT 9, DATE_SUB(NOW(), INTERVAL 3 DAY), 4.700, 1.400, 70.00 UNION ALL
SELECT 9, DATE_SUB(NOW(), INTERVAL 2 DAY), 4.900, 1.700, 73.00 UNION ALL
SELECT 9, DATE_SUB(NOW(), INTERVAL 1 DAY), 4.250, 1.800, 68.00 UNION ALL
SELECT 9, NOW(), 4.150, 1.650, 66.00;
