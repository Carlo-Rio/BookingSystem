package com.booking.system.v1.exception;

public class UserActiveException extends RuntimeException {
    public UserActiveException(String message) {
        super(message);
    }
}
