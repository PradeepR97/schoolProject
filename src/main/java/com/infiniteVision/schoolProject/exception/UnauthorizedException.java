package com.infiniteVision.schoolProject.exception;

/**
 * Caller is not authenticated or token is invalid.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}
