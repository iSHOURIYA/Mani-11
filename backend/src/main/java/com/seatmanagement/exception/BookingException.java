package com.seatmanagement.exception;

import org.springframework.http.HttpStatus;

public class BookingException extends RuntimeException {

    private final HttpStatus status;

    public BookingException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
