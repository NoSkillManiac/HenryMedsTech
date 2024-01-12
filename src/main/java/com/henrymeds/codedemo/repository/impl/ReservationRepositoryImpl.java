package com.henrymeds.codedemo.repository.impl;

import com.henrymeds.codedemo.dto.ReservationSlot;
import com.henrymeds.codedemo.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReservationRepositoryImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean createReservation(ReservationSlot reservation) {
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO RESERVATION (reservation_id, appointment_id, client_name, confirmation_sent_at, confirmed)
                    VALUES (?, ?, ?, ?, ?);
                    """);
            statement.setString(1, reservation.getReservationId().toString());
            statement.setString(2, reservation.getAppointmentId().toString());
            statement.setString(3, reservation.getClientName());
            statement.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            statement.setBoolean(5, false);
            return statement.execute();
        } catch (SQLException sqlException) {
            log.error("Error inserting the reservation into the reservation table.");
            return false;
        }
    }

    @Override
    public ReservationSlot findById(UUID reservationId) {
        ReservationSlot result = null;
        final String baseSql = "SELECT * FROM RESERVATION WHERE reservation_id=?";
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(baseSql);
            statement.setString(1, reservationId.toString());
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result = ReservationSlot.builder()
                        .reservationId(UUID.fromString(rs.getString("reservation_id")))
                        .appointmentId(UUID.fromString(rs.getString("appointment_id")))
                        .clientName(rs.getString("client_name"))
                        .confirmationSentAt(rs.getTimestamp("confirmation_sent_at").toLocalDateTime())
                        .confirmed(rs.getBoolean("confirmed"))
                        .build();
            }
        } catch (SQLException sqlException) {
            log.error("Error retrieving the requested appointment. {}", (Object) sqlException.getStackTrace());
            return null;
        }
        return result;
    }

    @Override
    public boolean deleteById(UUID reservationId) {
        final String deleteQuery = "DELETE FROM RESERVATION WHERE appointment_id =?";
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(deleteQuery);
            statement.setString(1, reservationId.toString());
            return statement.execute();
        } catch (SQLException sqlException) {
            log.error("Error deleting the reservation from the reservation table.");
            return false;
        }
    }

    @Override
    public boolean updateConfirmationInReservation(ReservationSlot reservation) {
        final String updateQuery = "UPDATE RESERVATION SET confirmed =? WHERE appointment_id =?";
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setBoolean(1, reservation.isConfirmed());
            statement.setString(2, reservation.getReservationId().toString());
            return statement.execute();
        } catch (SQLException sqlException) {
            log.error("Error updating the reservation in the reservation table. ReservationId {}",
                    reservation.getReservationId());
            return false;
        }
    }
}
