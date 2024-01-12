package com.henrymeds.codedemo.exception;

public class NotEnoughTimeToReserveException extends RuntimeException {

    public NotEnoughTimeToReserveException(String s) {
        super(s);
    }
}
