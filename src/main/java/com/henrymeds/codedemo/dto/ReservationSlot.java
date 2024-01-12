package com.henrymeds.codedemo.dto;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("RESERVATION")
public class ReservationSlot{
    @Id
    @NonNull
    @Column("reservation_id")
    private UUID reservationId;
    @Column("client_name")
    private String clientName;
    @Column("confirmed")
    private boolean confirmed;
    @Column("confirmation_sent_at")
    private LocalDateTime confirmationSentAt;
    @NonNull
    @Column("appointment_id")
    private UUID appointmentId;
}
