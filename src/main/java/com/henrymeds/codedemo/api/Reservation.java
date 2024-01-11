package com.henrymeds.codedemo.api;

import com.henrymeds.codedemo.dto.ReservationRequest;
import com.henrymeds.codedemo.dto.ReservationSlot;
import com.henrymeds.codedemo.scheduler.ReservationScheduler;
import com.henrymeds.codedemo.dto.AppointmentSlot;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/reservation")
public class Reservation {

    private final ReservationScheduler reservationScheduler;

    public Reservation(final ReservationScheduler reservationScheduler) {
        this.reservationScheduler = reservationScheduler;
    }

    @PutMapping("/reserve")
    public ReservationSlot makeAppointment(@RequestBody @NonNull final ReservationRequest reservationRequest) {
        return reservationScheduler.reserveAppointment(reservationRequest);
    }

    @PutMapping("/confirmation")
    public ReservationSlot confirmAppointment(@RequestBody @NonNull final ReservationSlot reservationSlot) {
        return reservationScheduler.confirmAppointment(reservationSlot);
    }

}
