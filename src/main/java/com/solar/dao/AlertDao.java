package com.solar.dao;

import com.solar.model.Alert;

import java.util.List;

/**
 * Data Access Object interface for Alert entities.
 */
public interface AlertDao {
    boolean create(Alert alert);
    List<Alert> findByUserId(Long userId, int limit);
    List<Alert> findUnreadByUserId(Long userId);
    int countUnreadByUserId(Long userId);
    boolean markAsRead(Long alertId, Long userId);
    boolean markAllAsRead(Long userId);
    List<Alert> findAllRecent(int limit);
}
