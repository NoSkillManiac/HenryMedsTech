package com.henrymeds.codedemo.dto;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Data
@Table("PROVIDERS")
public class ProviderInfo {
    @Column("provider_id")
    @Id
    @NonNull
    private UUID providerId;
    @Column("provider_name")
    private String providerName;
}
