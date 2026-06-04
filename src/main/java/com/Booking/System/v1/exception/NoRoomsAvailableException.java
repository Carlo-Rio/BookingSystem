package com.booking.system.v1.exception;

public class NoRoomsAvailableException extends RuntimeException {
    public NoRoomsAvailableException(String message) {
        super(message);
    }
}
