package com.henrymeds.codedemo.repository;

import com.henrymeds.codedemo.dto.ReservationSlot;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface ReservationRepository extends CrudRepository<ReservationSlot, UUID> {
}
