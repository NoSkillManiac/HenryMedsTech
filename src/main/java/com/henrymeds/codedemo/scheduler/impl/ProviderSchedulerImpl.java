package com.henrymeds.codedemo.scheduler.impl;

import com.henrymeds.codedemo.dto.AppointmentSlot;
import com.henrymeds.codedemo.dto.ProviderAvailability;
import com.henrymeds.codedemo.exception.ProviderLookupFailedException;
import com.henrymeds.codedemo.repository.AppointmentRepository;
import com.henrymeds.codedemo.repository.ProviderRepository;
import com.henrymeds.codedemo.scheduler.ProviderScheduler;
import com.henrymeds.codedemo.util.DemoConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
public class ProviderSchedulerImpl implements ProviderScheduler {
    private final AppointmentRepository appointmentRepository;
    private final ProviderRepository providerRepository;

    @Autowired
    public ProviderSchedulerImpl(final AppointmentRepository appointmentRepository,
                                 final ProviderRepository providerRepository) {
        this.appointmentRepository = appointmentRepository;
        this.providerRepository = providerRepository;
    }
    @Override
    public List<AppointmentSlot> getAvailability(final UUID providerId, final LocalDate requestedDate) {
        return appointmentRepository.findAppointmentsForProviderOnDate(providerId, requestedDate);
    }

    @Override
    public Integer updateAvailability(final ProviderAvailability providerAvailability) {
        if (providerRepository.findById(providerAvailability.getProviderId()).isEmpty()) {
            throw new ProviderLookupFailedException("Provider lookup failed.");
        }
        List<AppointmentSlot> appointments = generateAppointmentSlots(providerAvailability);
        if (!appointments.isEmpty()) {
            appointmentRepository.addOrUpdateAppointments(appointments);
        }
        return appointments.size();
    }

    private List<AppointmentSlot> generateAppointmentSlots(final ProviderAvailability providerAvailability) {
        final List<AppointmentSlot> appointments = new ArrayList<>();
        LocalTime endMarkerTime = providerAvailability.getStartTime().plusMinutes(DemoConstants.APPOINTMENT_DURATION);
        LocalTime startMarkerTime = providerAvailability.getStartTime();
        while (endMarkerTime.isBefore(providerAvailability.getEndTime())) {
            AppointmentSlot timeSlot = AppointmentSlot.builder()
                    .appointmentId(UUID.randomUUID())
                    .appointmentDate(providerAvailability.getDate())
                    .providerId(providerAvailability.getProviderId())
                    .startTime(startMarkerTime)
                    .endTime(endMarkerTime)
                    .build();
            endMarkerTime = endMarkerTime.plusMinutes(DemoConstants.APPOINTMENT_DURATION);
            startMarkerTime = startMarkerTime.plusMinutes(DemoConstants.APPOINTMENT_DURATION);
            appointments.add(timeSlot);
        }
        log.info("{} appointments generated for providerId {}", appointments.size(), providerAvailability.getProviderId());
        return appointments;
    }
}
