package com.henrymeds.codedemo.scheduler.impl

import com.henrymeds.codedemo.dto.AppointmentSlot
import com.henrymeds.codedemo.dto.ProviderAvailability
import com.henrymeds.codedemo.repository.AppointmentRepository
import com.henrymeds.codedemo.repository.ProviderRepository
import groovy.util.logging.Slf4j
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class ProviderSchedulerImplSpec extends Specification {

    ProviderSchedulerImpl providerScheduler

    def setup() {
        providerScheduler = new ProviderSchedulerImpl(Mock(AppointmentRepository), Mock(ProviderRepository))
    }

    def "Need"() {
        given:
        System.out.println(LocalDate.now())
        System.out.println(LocalTime.now())
        System.out.println(LocalDateTime.now())
        System.out.println(buildProviderAvailability(LocalTime.now(), LocalTime.now()))
        expect:
        true
    }

    def "Ensure passthrough is called when the requesting fields are not null"() {
        when:
        List<AppointmentSlot> result = providerScheduler.getAvailability(providerId, date)

        then:
        numInvocations * providerScheduler.appointmentRepository.findAppointmentsForProviderOnDate(_ as UUID, _ as LocalDate) >>
                List.of(AppointmentSlot.builder()
                        .startTime(LocalTime.now())
                        .build())
        expectedSize == result.size()

        where:
        providerId        | date            || numInvocations | expectedSize
        UUID.randomUUID() | LocalDate.now() || 1              | 1
        UUID.randomUUID() | null            || 0              | 0
        null              | LocalDate.now() || 0              | 0

    }

    def "Confirm Appointment slots generate as expected"() {
        when:
        List<AppointmentSlot> appointments =
                providerScheduler.generateAppointmentSlots(buildProviderAvailability(startTime, endTime))

        then:
        expectedAppointments == appointments.size()

        where:
        // Expected appointments would ideally be dynamically calculated in this instance. But that takes a little more
        // time than this assessment provides.
        startTime          | endTime                       || expectedAppointments
        LocalTime.MIDNIGHT | LocalTime.NOON                || 47
        LocalTime.now()    | LocalTime.now()               || 0
        LocalTime.MIDNIGHT | LocalTime.ofSecondOfDay(950)  || 1
        LocalTime.MIDNIGHT | LocalTime.ofSecondOfDay(8753) || 9
        //Because Midnight is before noon, there should be no appointments generated.
        LocalTime.NOON     | LocalTime.MIDNIGHT            || 0
    }

    def "Ensure providers generate availability if they exist in the system and there are appointments in the given time"() {
        given:
        ProviderAvailability providerAvailability = buildProviderAvailability(startTime, endTime)

        when:
        providerScheduler.updateAvailability(providerAvailability)

        then:
        1 * providerScheduler.providerRepository.checkProviderExists(providerAvailability.getProviderId()) >> providerLookup
        numAppointmentCalls * providerScheduler.appointmentRepository.addOrUpdateAppointments(_ as List)

        where:
        providerLookup | startTime          | endTime            || numAppointmentCalls
        true           | LocalTime.MIDNIGHT | LocalTime.NOON     || 1
        true           | LocalTime.now()    | LocalTime.now()    || 0
        true           | LocalTime.NOON     | LocalTime.MIDNIGHT || 0
        false          | LocalTime.MIDNIGHT | LocalTime.NOON     || 0
        false          | LocalTime.now()    | LocalTime.now()    || 0
        false          | LocalTime.NOON     | LocalTime.MIDNIGHT || 0

    }

    def buildProviderAvailability(LocalTime startTime, LocalTime endTime) {
        return ProviderAvailability.builder()
                .providerId(UUID.randomUUID())
                .providerName("Dr. Hyde")
                .date(LocalDate.now())
                .startTime(startTime)
                .endTime(endTime)
                .build()
    }

}
