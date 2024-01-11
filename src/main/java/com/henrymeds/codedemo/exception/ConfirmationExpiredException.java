package com.henrymeds.codedemo.exception;

public class ConfirmationExpiredException extends RuntimeException {

    public ConfirmationExpiredException(final String s) {
        super(s);
    }
}
