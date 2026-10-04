package com.solar.exception;

/**
 * Thrown when credentials fail authentication or session is invalid.
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }
}
