package com.booking.system.v1.exception;

public class NoRoomsFoundException extends RuntimeException {
    public NoRoomsFoundException(String message) {
        super(message);
    }
}
