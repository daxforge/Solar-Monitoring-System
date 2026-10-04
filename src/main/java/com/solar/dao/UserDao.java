package com.solar.dao;

import com.solar.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDao {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    List<User> search(String keyword);
    boolean create(User user);
    boolean update(User user);
    boolean updatePassword(Long userId, String passwordHash);
    boolean deleteWithCascade(Long userId);
    long countUsers();
}
