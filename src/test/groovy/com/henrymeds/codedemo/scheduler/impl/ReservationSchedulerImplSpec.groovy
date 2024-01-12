package com.henrymeds.codedemo.scheduler.impl

import com.henrymeds.codedemo.dto.AppointmentSlot
import com.henrymeds.codedemo.dto.ReservationRequest
import com.henrymeds.codedemo.dto.ReservationSlot
import com.henrymeds.codedemo.exception.ConfirmationExpiredException
import com.henrymeds.codedemo.exception.NotEnoughTimeToReserveException
import com.henrymeds.codedemo.exception.ReservationNotFoundException
import com.henrymeds.codedemo.repository.AppointmentRepository
import com.henrymeds.codedemo.repository.ReservationRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ReservationSchedulerImplSpec extends Specification {

    ReservationSchedulerImpl reservationScheduler

    def setup() {
        reservationScheduler = new ReservationSchedulerImpl(Mock(ReservationRepository), Mock(AppointmentRepository))
    }

    def "Reservation request is saved to the database"() {
        given:
        ReservationRequest request = buildReservationRequest()
        ReservationSlot expectedSlot = buildReservationSlot(request)

        when:
        ReservationSlot actual = reservationScheduler.reserveAppointment(request)

        then:
        1 * reservationScheduler.reservationRepository.createReservation(_ as ReservationSlot)
        1 * reservationScheduler.appointmentRepository.findAppointmentById(_ as UUID) >> buildAppointment(request.appointmentId)
        expectedSlot.getAppointmentId() == actual.getAppointmentId()
        expectedSlot.getClientName() == actual.getClientName()
    }

    def "Reservation fails to be made"() {
        given:
        ReservationRequest request = buildReservationRequest()
        request.clientTime = LocalDateTime.now()

        when:
        reservationScheduler.reserveAppointment(request)

        then:
        1 * reservationScheduler.appointmentRepository.findAppointmentById(_ as UUID) >> appointment
        thrown(expectedException)

        where:
        appointment                         || expectedException
        null                                || ReservationNotFoundException.class
        buildAppointment(UUID.randomUUID()) || NotEnoughTimeToReserveException.class


    }

    def "Confirmation of reservation fails"() {
        given:
        ReservationSlot reservation = buildReservationSlot()
        reservation.setConfirmationSentAt(confirmationTime)

        when:
        reservationScheduler.confirmAppointment(reservation)

        then:
        1 * reservationScheduler.reservationRepository.findById(reservation.getReservationId()) >> findReturn
        numInvocations * reservationScheduler.reservationRepository.deleteById(reservation.getReservationId())
        0 * reservationScheduler.reservationRepository.updateConfirmationInReservation(_ as ReservationSlot)
        thrown(expectedException)

        where:
        confirmationTime    | findReturn             || numInvocations | expectedException
        LocalDateTime.now() | null                   || 0              | ReservationNotFoundException.class
        LocalDateTime.MIN   | null                   || 0              | ReservationNotFoundException.class
        LocalDateTime.MIN   | buildReservationSlot() || 1              | ConfirmationExpiredException.class
    }

    def "Confirmation of reservation is successful"() {
        given:
        ReservationSlot reservation = buildReservationSlot()

        when:
        ReservationSlot modified = reservationScheduler.confirmAppointment(reservation)

        then:
        1 * reservationScheduler.reservationRepository.findById(reservation.getReservationId()) >> reservation
        1 * reservationScheduler.reservationRepository.updateConfirmationInReservation(_ as ReservationSlot)
        modified.isConfirmed()
        reservation.reservationId == modified.reservationId
    }

    def buildAppointment(UUID appointmentId) {
        return AppointmentSlot.builder()
                .appointmentId(appointmentId)
                .endTime(LocalTime.now().plusMinutes(15))
                .startTime(LocalTime.now())
                .appointmentDate(LocalDate.now())
                .providerId(UUID.randomUUID())
                .build()
    }

    def buildReservationRequest() {
        ReservationRequest request = new ReservationRequest()
        request.setAppointmentId(UUID.randomUUID())
        request.setClientTime(LocalDateTime.MIN)
        request.setClientName("Josh Auer")
        return request
    }

    def buildReservationSlot(ReservationRequest request) {
        return ReservationSlot.builder()
                .clientName(request.getClientName())
                .appointmentId(request.getAppointmentId())
                .reservationId(UUID.randomUUID())
                .confirmed(false)
                .confirmationSentAt(LocalDateTime.now())
                .build()
    }

    def buildReservationSlot() {
        return buildReservationSlot(buildReservationRequest())
    }

}
