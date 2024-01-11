package com.henrymeds.codedemo.scheduler.impl;

import com.henrymeds.codedemo.dto.ReservationRequest;
import com.henrymeds.codedemo.dto.ReservationSlot;
import com.henrymeds.codedemo.exception.ConfirmationExpiredException;
import com.henrymeds.codedemo.exception.ReservationNotFoundException;
import com.henrymeds.codedemo.repository.ReservationRepository;
import com.henrymeds.codedemo.scheduler.ReservationScheduler;
import com.henrymeds.codedemo.util.DemoConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReservationSchedulerImpl implements ReservationScheduler {

    private final ReservationRepository reservationRepository;

    public ReservationSchedulerImpl(final ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }
    @Override
    public ReservationSlot reserveAppointment(final ReservationRequest reservationRequest) {
        //In a real-world implementation, I would preform a lookup on the AppointmentId prior creating a reservation.
        ReservationSlot reservation = ReservationSlot.builder()
                .reservationId(UUID.randomUUID())
                .appointmentId(reservationRequest.getAppointmentId())
                .clientName(reservationRequest.getClientName())
                .confirmed(false)
                .confirmationSentAt(LocalDateTime.now())
                .build();
        reservationRepository.save(reservation);
        return reservation;
    }

    public ReservationSlot confirmAppointment(final ReservationSlot reservation) {
        if (reservationRepository.findById(reservation.getReservationId()).isEmpty()) {
            throw new ReservationNotFoundException("Unable to find reservation requested. Please try again.");
        }
        if (LocalDateTime.now().isAfter(reservation.getConfirmationSentAt().plusMinutes(DemoConstants.EXPIRATION_MINUTES))) {
            reservationRepository.deleteById(reservation.getReservationId());
            throw new ConfirmationExpiredException("Confirmation window expired, releasing reservation.");
        }
        reservation.setConfirmed(true);
        reservationRepository.save(reservation);
        return reservation;
    }
}
