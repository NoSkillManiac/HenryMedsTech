package com.henrymeds.codedemo.api;

import com.henrymeds.codedemo.exception.ConfirmationExpiredException;
import com.henrymeds.codedemo.exception.NotEnoughTimeToReserveException;
import com.henrymeds.codedemo.exception.ProviderLookupFailedException;
import com.henrymeds.codedemo.exception.ReservationNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@Slf4j
@ControllerAdvice
@Component
@ComponentScan("com.henrymeds.codedemo")
public class ExceptionController {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({ReservationNotFoundException.class, ProviderLookupFailedException.class})
    public String handleReservationNotFound(HttpServletRequest req, HttpServletResponse resp, Exception e) throws IOException {
        log.error(e.getMessage());
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ConfirmationExpiredException.class, NotEnoughTimeToReserveException.class})
    public String handleConfirmationException(HttpServletRequest req, HttpServletResponse resp, Exception e) {
        log.error(e.getMessage());
        return e.getMessage();
    }

}
