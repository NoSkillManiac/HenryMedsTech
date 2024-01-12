package com.henrymeds.codedemo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReservationRequest {
    private String clientName;
    private UUID appointmentId;
    private LocalDateTime clientTime;
}
