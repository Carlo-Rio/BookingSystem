package com.booking.system.v1.exception;

public class InvalidRoomNumberException extends RuntimeException {
    public InvalidRoomNumberException(String message) {
        super(message);
    }
}
