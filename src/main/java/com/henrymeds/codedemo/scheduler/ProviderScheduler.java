package com.henrymeds.codedemo.scheduler;

import com.henrymeds.codedemo.dto.AppointmentSlot;
import com.henrymeds.codedemo.dto.ProviderAvailability;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ProviderScheduler {

    List<AppointmentSlot> getAvailability(final UUID providerId, final LocalDate requestedDate);

    Integer updateAvailability(final ProviderAvailability providerAvailability);
}
