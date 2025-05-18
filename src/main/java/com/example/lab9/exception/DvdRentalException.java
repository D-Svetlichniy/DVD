package com.example.lab9.exception;

public class DvdRentalException extends RuntimeException {

    public DvdRentalException(String message) {
        super(message);
    }

    public DvdRentalException(String message, Throwable cause) {
        super(message, cause);
    }
}