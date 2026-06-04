package com.booking.system.v1.exception;

public class InvalidPasswordException extends  RuntimeException{
    public InvalidPasswordException(String message) {
        super(message);
    }
}


