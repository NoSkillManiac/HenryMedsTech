package com.henrymeds.codedemo.scheduler;

import com.henrymeds.codedemo.dto.ReservationRequest;
import com.henrymeds.codedemo.dto.ReservationSlot;
import com.henrymeds.codedemo.exception.ConfirmationExpiredException;
import com.henrymeds.codedemo.exception.ReservationNotFoundException;

public interface ReservationScheduler {

    ReservationSlot reserveAppointment(ReservationRequest reservation);
    ReservationSlot confirmAppointment(ReservationSlot reservationSlot) throws ReservationNotFoundException, ConfirmationExpiredException;
}
