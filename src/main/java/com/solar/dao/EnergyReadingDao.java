package com.solar.dao;

import com.solar.model.EnergyReading;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Data Access Object interface for High-Frequency Energy Telemetry Readings.
 * Features time-series querying, aggregation functions (SUM/AVG by hour/day), and KPI analytics.
 */
public interface EnergyReadingDao {
    boolean insert(EnergyReading reading);
    Optional<EnergyReading> findLatestByDeviceId(Long deviceId);
    List<EnergyReading> findRecentByDeviceId(Long deviceId, int limit);
    List<EnergyReading> findByDeviceIdAndDateRange(Long deviceId, LocalDateTime start, LocalDateTime end);
    List<Map<String, Object>> getAggregatedByHour(Long ownerId, Long deviceId, LocalDateTime start, LocalDateTime end);
    List<Map<String, Object>> getAggregatedByDay(Long ownerId, Long deviceId, LocalDateTime start, LocalDateTime end);
    Map<String, Object> getSystemSummaryToday();
    Map<String, Object> getHomeownerSummaryToday(Long ownerId);
    List<Map<String, Object>> getConsumptionReport(Long ownerId, LocalDateTime start, LocalDateTime end);
}
