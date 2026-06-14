package com.booking.system.v1.exception;

public class OverlappingReservationException extends RuntimeException {
    public OverlappingReservationException(String message) {
        super(message);
    }
}
