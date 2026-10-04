package com.solar.dao;

import com.solar.model.StorageStatus;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Battery Storage Status entities.
 */
public interface StorageStatusDao {
    Optional<StorageStatus> findByDeviceId(Long deviceId);
    List<StorageStatus> findByOwnerId(Long ownerId);
    boolean upsert(StorageStatus status);
}
