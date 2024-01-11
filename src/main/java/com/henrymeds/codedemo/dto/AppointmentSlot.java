package com.henrymeds.codedemo.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("APPOINTMENT_SLOT")
public class AppointmentSlot {
    @Id
    @Column("appointment_id")
    @NonNull
    private UUID appointmentId;
    @Column("provider_id")
    @NonNull
    private UUID providerId;
    @Column("appointment_date")
    private LocalDate appointmentDate;
    @Column("start_time")
    private LocalTime startTime;
    @Column("end_time")
    private LocalTime endTime;
}
