package com.henrymeds.codedemo.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(final String s) {
        super(s);
    }
}
