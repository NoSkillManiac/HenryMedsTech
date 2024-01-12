package com.henrymeds.codedemo.scheduler.impl;

import com.henrymeds.codedemo.dto.AppointmentSlot;
import com.henrymeds.codedemo.dto.ReservationRequest;
import com.henrymeds.codedemo.dto.ReservationSlot;
import com.henrymeds.codedemo.exception.ConfirmationExpiredException;
import com.henrymeds.codedemo.exception.NotEnoughTimeToReserveException;
import com.henrymeds.codedemo.exception.ReservationNotFoundException;
import com.henrymeds.codedemo.repository.AppointmentRepository;
import com.henrymeds.codedemo.repository.ReservationRepository;
import com.henrymeds.codedemo.scheduler.ReservationScheduler;
import com.henrymeds.codedemo.util.DemoConstants;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class ReservationSchedulerImpl implements ReservationScheduler {

    private final ReservationRepository reservationRepository;
    private final AppointmentRepository appointmentRepository;

    public ReservationSchedulerImpl(final ReservationRepository reservationRepository,
                                    final AppointmentRepository appointmentRepository) {
        this.reservationRepository = reservationRepository;
        this.appointmentRepository = appointmentRepository;
    }
    @Override
    public ReservationSlot reserveAppointment(final ReservationRequest reservationRequest) {
        AppointmentSlot appointment = appointmentRepository.findAppointmentById(reservationRequest.getAppointmentId());
        if (appointment == null) {
            throw new ReservationNotFoundException("Reservation can't be made because the appointment doesn't exist");
        }
        if (!isValidBookingTime(appointment, reservationRequest.getClientTime())) {
            throw new NotEnoughTimeToReserveException("This appointment is within a 24 hour window. As such, the appointment" +
                    " cannot be booked.");
        }
        ReservationSlot reservation = ReservationSlot.builder()
                .reservationId(UUID.randomUUID())
                .appointmentId(reservationRequest.getAppointmentId())
                .clientName(reservationRequest.getClientName())
                .confirmed(false)
                .confirmationSentAt(LocalDateTime.now())
                .build();
        reservationRepository.createReservation(reservation);
        return reservation;
    }

    public ReservationSlot confirmAppointment(final ReservationSlot reservation) {
        if (reservationRepository.findById(reservation.getReservationId()) == null) {
            throw new ReservationNotFoundException("Unable to find reservation requested. Please try again.");
        }
        if (LocalDateTime.now().isAfter(reservation.getConfirmationSentAt().plusMinutes(DemoConstants.EXPIRATION_MINUTES))) {
            reservationRepository.deleteById(reservation.getReservationId());
            throw new ConfirmationExpiredException("Confirmation window expired, releasing reservation.");
        }
        reservation.setConfirmed(true);
        reservationRepository.updateConfirmationInReservation(reservation);
        return reservation;
    }

    private boolean isValidBookingTime(final AppointmentSlot appointment, final LocalDateTime clientTime) {
        LocalDateTime appointmentTime = LocalDateTime.of(appointment.getAppointmentDate(), appointment.getStartTime());
        return clientTime.plusHours(24).isBefore(appointmentTime);
    }
}
