package com.henrymeds.codedemo.repository;

import com.henrymeds.codedemo.dto.AppointmentSlot;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository{

     int[] addOrUpdateAppointments(final List<AppointmentSlot> appointments);
     List<AppointmentSlot> findAppointmentsForProviderOnDate(final UUID providerID, final LocalDate date);

     AppointmentSlot findAppointmentById(final UUID appointmentId);
}
