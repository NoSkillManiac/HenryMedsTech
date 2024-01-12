package com.henrymeds.codedemo.scheduler.impl

import com.henrymeds.codedemo.dto.AppointmentSlot
import com.henrymeds.codedemo.dto.ProviderAvailability
import com.henrymeds.codedemo.dto.ProviderInfo
import com.henrymeds.codedemo.exception.ProviderLookupFailedException
import com.henrymeds.codedemo.repository.AppointmentRepository
import com.henrymeds.codedemo.repository.ProviderRepository
import spock.lang.Specification

import java.time.LocalDate
import java.time.LocalTime

class ProviderSchedulerImplSpec extends Specification {

    ProviderSchedulerImpl providerScheduler

    def setup() {
        providerScheduler = new ProviderSchedulerImpl(Mock(AppointmentRepository), Mock(ProviderRepository))
    }

    def "Ensure passthrough is called when the requesting fields are not null"() {
        when:
        providerScheduler.getAvailability(UUID.randomUUID(), LocalDate.now())

        then:
        1 * providerScheduler.appointmentRepository.findAppointmentsForProviderOnDate(_ as UUID, _ as LocalDate)

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
        1 * providerScheduler.providerRepository.findById(providerAvailability.getProviderId()) >> providerLookup
        numAppointmentCalls * providerScheduler.appointmentRepository.addOrUpdateAppointments(_ as List)

        where:
        providerLookup                                   | startTime          | endTime            || numAppointmentCalls
        Optional.of(new ProviderInfo(UUID.randomUUID())) | LocalTime.MIDNIGHT | LocalTime.NOON     || 1
        Optional.of(new ProviderInfo(UUID.randomUUID())) | LocalTime.now()    | LocalTime.now()    || 0
        Optional.of(new ProviderInfo(UUID.randomUUID())) | LocalTime.NOON     | LocalTime.MIDNIGHT || 0
    }

    def "Ensure providers generate availability if they exist in the system and there are appointments in the given time"() {
        given:
        ProviderAvailability providerAvailability = buildProviderAvailability(LocalTime.MIDNIGHT, LocalTime.NOON)

        when:
        providerScheduler.updateAvailability(providerAvailability)

        then:
        1 * providerScheduler.providerRepository.findById(providerAvailability.getProviderId()) >> Optional.empty()
        thrown(ProviderLookupFailedException.class)


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
