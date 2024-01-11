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
public class ReservationSlot {
    @Id
    @NonNull
    @Column
    private UUID reservationId;
    @Column
    private String clientName;
    @Column
    private boolean confirmed;
    @Column
    private LocalDateTime confirmationSentAt;
    @NonNull
    @Column
    private UUID appointmentId;
}
