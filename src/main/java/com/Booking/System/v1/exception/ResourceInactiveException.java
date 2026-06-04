package com.booking.system.v1.exception;

public class ResourceInactiveException extends RuntimeException {
    public ResourceInactiveException(String message) {
        super(message);
    }
}
