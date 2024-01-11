package com.henrymeds.codedemo.exception;

public class ProviderLookupFailedException extends RuntimeException{

    public ProviderLookupFailedException(final String s) {
        super(s);
    }
}
