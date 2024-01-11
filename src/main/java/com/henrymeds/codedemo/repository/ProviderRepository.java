package com.henrymeds.codedemo.repository;

import com.henrymeds.codedemo.dto.ProviderInfo;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ProviderRepository extends CrudRepository<ProviderInfo, UUID> {
}
