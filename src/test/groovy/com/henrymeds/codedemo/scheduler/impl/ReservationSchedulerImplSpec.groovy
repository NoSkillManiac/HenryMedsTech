package com.henrymeds.codedemo.scheduler.impl

import com.henrymeds.codedemo.dto.ReservationRequest
import com.henrymeds.codedemo.dto.ReservationSlot
import com.henrymeds.codedemo.exception.ConfirmationExpiredException
import com.henrymeds.codedemo.exception.ReservationNotFoundException
import com.henrymeds.codedemo.repository.ReservationRepository
import spock.lang.Specification

import java.time.LocalDateTime

class ReservationSchedulerImplSpec extends Specification {

    ReservationSchedulerImpl reservationScheduler

    def setup() {
        reservationScheduler = new ReservationSchedulerImpl(Mock(ReservationRepository))
    }

    def "Reservation request is saved to the database"() {
        given:
        ReservationRequest request = buildReservationRequest()
        ReservationSlot expectedSlot = buildReservationSlot(request)

        when:
        ReservationSlot actual = reservationScheduler.reserveAppointment(request)

        then:
        1 * reservationScheduler.reservationRepository.save(_ as ReservationSlot)
        expectedSlot.getAppointmentId() == actual.getAppointmentId()
        expectedSlot.getClientName() == actual.getClientName()

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
        0 * reservationScheduler.reservationRepository.save(_ as ReservationSlot)
        thrown(expectedException)

        where:
        confirmationTime    | findReturn                          || numInvocations | expectedException
        LocalDateTime.now() | Optional.empty()                    || 0              | ReservationNotFoundException.class
        LocalDateTime.MIN   | Optional.empty()                    || 0              | ReservationNotFoundException.class
        LocalDateTime.MIN   | Optional.of(buildReservationSlot()) || 1              | ConfirmationExpiredException.class
    }

    def "Confirmation of reservation is successful"() {
        given:
        ReservationSlot reservation = buildReservationSlot()

        when:
        ReservationSlot modified = reservationScheduler.confirmAppointment(reservation)

        then:
        1 * reservationScheduler.reservationRepository.findById(reservation.getReservationId())
        1 * reservationScheduler.reservationRepository.save(_ as ReservationSlot)
        modified.isConfirmed()
        reservation.reservationId == modified.reservationId
    }

    def buildReservationRequest() {
        ReservationRequest request = new ReservationRequest()
        request.setAppointmentId(UUID.randomUUID())
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
