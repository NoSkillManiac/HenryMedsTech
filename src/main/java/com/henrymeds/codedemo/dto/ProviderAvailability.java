package com.henrymeds.codedemo.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderAvailability {
    @NonNull
    private UUID providerId;
    private String providerName;
    private LocalDate date;
    @NonNull
    private LocalTime startTime;
    @NonNull
    private LocalTime endTime;
}