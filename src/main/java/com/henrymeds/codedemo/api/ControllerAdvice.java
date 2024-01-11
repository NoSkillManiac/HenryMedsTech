package com.henrymeds.codedemo.api;

import com.henrymeds.codedemo.exception.ConfirmationExpiredException;
import com.henrymeds.codedemo.exception.ProviderLookupFailedException;
import com.henrymeds.codedemo.exception.ReservationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ReservationNotFoundException.class, ProviderLookupFailedException.class})
    public String handleReservationNotFound(HttpServletRequest req, Exception e) {
        return e.toString();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConfirmationExpiredException.class)
    public String handleConfirmationException(HttpServletRequest req, ConfirmationExpiredException e) {
        return e.toString();
    }

}
