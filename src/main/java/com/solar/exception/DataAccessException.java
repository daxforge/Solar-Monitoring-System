package com.solar.exception;

/**
 * Thrown when low-level JDBC or database operations fail, isolating persistence logic.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
