package com.solar.service;

import com.solar.dao.UserDao;
import com.solar.dao.impl.UserDaoImpl;
import com.solar.exception.AuthenticationException;
import com.solar.exception.ValidationException;
import com.solar.model.User;
import com.solar.util.PasswordUtil;
import com.solar.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Service encapsulating authentication, session credential verification and password lifecycle.
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserDao userDao;

    public AuthService() {
        this.userDao = new UserDaoImpl();
    }

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Authenticates a user against email and plaintext password.
     * Uses BCrypt verification with timing-resistant matching.
     */
    public User authenticate(String email, String plainPassword) {
        ValidationUtil.requireNonBlank(email, "Email");
        ValidationUtil.requireNonBlank(plainPassword, "Password");

        Optional<User> optionalUser = userDao.findByEmail(email.trim().toLowerCase());
        if (optionalUser.isEmpty()) {
            logger.warn("Authentication failed: email not found -> {}", email);
            throw new AuthenticationException("Invalid email or password.");
        }

        User user = optionalUser.get();
        if (!user.isActive()) {
            logger.warn("Authentication failed: inactive account -> {}", email);
            throw new AuthenticationException("This account is currently deactivated. Please contact an administrator.");
        }

        if (!PasswordUtil.checkPassword(plainPassword, user.getPasswordHash())) {
            logger.warn("Authentication failed: password mismatch for -> {}", email);
            throw new AuthenticationException("Invalid email or password.");
        }

        logger.info("User successfully authenticated: {} ({})", user.getEmail(), user.getRole());
        return user;
    }

    /**
     * Updates an authenticated user's password with validation of the current password.
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        ValidationUtil.requireNonBlank(oldPassword, "Current Password");
        ValidationUtil.validatePassword(newPassword);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new AuthenticationException("User session not found"));

        if (!PasswordUtil.checkPassword(oldPassword, user.getPasswordHash())) {
            throw new ValidationException("Current password does not match our records.");
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        userDao.updatePassword(userId, newHash);
        logger.info("Password successfully changed for user ID {}", userId);
    }
}
