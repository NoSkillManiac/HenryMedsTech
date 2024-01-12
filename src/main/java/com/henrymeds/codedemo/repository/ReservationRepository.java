package com.henrymeds.codedemo.repository;

import com.henrymeds.codedemo.dto.ReservationSlot;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository {

    boolean createReservation(ReservationSlot reservation);
    ReservationSlot findById(UUID reservationId);
    boolean deleteById(UUID reservationId);
    boolean updateConfirmationInReservation(ReservationSlot reservation);
}
