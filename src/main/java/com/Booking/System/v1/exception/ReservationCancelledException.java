package com.booking.system.v1.exception;

public class ReservationCancelledException extends RuntimeException {
    public ReservationCancelledException(String message) {
        super(message);
    }
}
