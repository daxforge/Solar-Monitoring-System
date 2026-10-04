package com.solar.service;

import com.solar.dao.UserDao;
import com.solar.dao.impl.UserDaoImpl;
import com.solar.enums.Role;
import com.solar.exception.ValidationException;
import com.solar.model.User;
import com.solar.util.PasswordUtil;
import com.solar.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service managing user lifecycle, administrative provisioning and role-based profiles.
 */
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;

    public UserService() {
        this.userDao = new UserDaoImpl();
    }

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public List<User> searchUsers(String query) {
        if (query == null || query.trim().isEmpty()) {
            return userDao.findAll();
        }
        return userDao.search(query);
    }

    public User getUserById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new ValidationException("User not found with ID: " + id));
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userDao.findByEmail(email.trim().toLowerCase());
    }

    /**
     * Creates a new user with email uniqueness validation and BCrypt password encryption.
     */
    public User createUser(String name, String email, String password, Role role) {
        ValidationUtil.requireNonBlank(name, "Full Name");
        ValidationUtil.validateEmail(email);
        ValidationUtil.validatePassword(password);

        String normalizedEmail = email.trim().toLowerCase();
        if (userDao.findByEmail(normalizedEmail).isPresent()) {
            throw new ValidationException("A user with email '" + normalizedEmail + "' already exists.");
        }

        String passwordHash = PasswordUtil.hashPassword(password);
        User user = new User(null, name.trim(), normalizedEmail, passwordHash, role != null ? role : Role.HOMEOWNER, LocalDateTime.now(), true);
        userDao.create(user);
        logger.info("Successfully provisioned new user: {} (Role: {})", user.getEmail(), user.getRole());
        return user;
    }

    /**
     * Updates profile details with unique email verification.
     */
    public void updateUser(Long id, String name, String email, Role role, boolean active) {
        ValidationUtil.requireNonBlank(name, "Full Name");
        ValidationUtil.validateEmail(email);

        User existing = getUserById(id);
        String normalizedEmail = email.trim().toLowerCase();

        // Check if updating to an email that belongs to another user
        Optional<User> emailHolder = userDao.findByEmail(normalizedEmail);
        if (emailHolder.isPresent() && !emailHolder.get().getId().equals(id)) {
            throw new ValidationException("Email '" + normalizedEmail + "' is already registered to another account.");
        }

        existing.setName(name.trim());
        existing.setEmail(normalizedEmail);
        if (role != null) existing.setRole(role);
        existing.setActive(active);

        userDao.update(existing);
        logger.info("User ID {} updated successfully.", id);
    }

    /**
     * Updates personal profile for homeowners (name and email).
     */
    public void updateProfile(Long id, String name, String email) {
        User user = getUserById(id);
        updateUser(id, name, email, user.getRole(), user.isActive());
    }

    /**
     * Deletes user with transactional cascade cleanup.
     */
    public void deleteUser(Long id) {
        getUserById(id); // Ensure user exists
        userDao.deleteWithCascade(id);
        logger.info("User ID {} deleted with cascade cleanup.", id);
    }

    public long getActiveUserCount() {
        return userDao.countUsers();
    }
}
