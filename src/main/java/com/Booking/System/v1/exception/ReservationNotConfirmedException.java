package com.booking.system.v1.exception;

public class ReservationNotConfirmedException extends RuntimeException {
    public ReservationNotConfirmedException(String message) {
        super(message);
    }
}
