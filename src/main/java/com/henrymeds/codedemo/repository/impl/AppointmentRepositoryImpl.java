package com.henrymeds.codedemo.repository.impl;

import com.henrymeds.codedemo.dto.AppointmentSlot;
import com.henrymeds.codedemo.repository.AppointmentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Repository
public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AppointmentRepositoryImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Modifying
    @Override
    public boolean addOrUpdateAppointments(final List<AppointmentSlot> appointments) {
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO APPOINTMENT_SLOT (appointment_id, provider_id, appointment_date, start_time, end_time)
                    VALUES (?, ?, ?, ?, ?);
                    """);
            for (AppointmentSlot appointment : appointments) {
                statement.setString(1, appointment.getAppointmentId().toString());
                statement.setString(2, appointment.getProviderId().toString());
                statement.setDate(3, Date.valueOf(appointment.getAppointmentDate()));
                statement.setTime(4, Time.valueOf(appointment.getStartTime()));
                statement.setTime(5, Time.valueOf(appointment.getEndTime()));
                statement.addBatch();
            }
            return statement.execute();
        } catch (SQLException sqlException) {
            log.error("Error inserting the appointments into the appointmentSlots.");
            return false;
        }
    }
    @Override
    public List<AppointmentSlot> findAppointmentsForProviderOnDate(final UUID providerID, final LocalDate date) {
        List<AppointmentSlot> appointments = new ArrayList<>();
        ResultSet rs;
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            PreparedStatement statement = connection.prepareStatement("""
                SELECT *
                FROM APP_DEMO.APPOINTMENT_SLOT
                WHERE provider_id=?
                AND appointment_date=?"
                """);
            statement.setString(1, providerID.toString());
            statement.setDate(2, Date.valueOf(date));
            rs = statement.executeQuery();

            // Map over to the AppointmentSlot format- I would find a better  way to do this, given team input and more time.
            while(rs.next()) {
                appointments.add(AppointmentSlot.builder()
                        .appointmentId(UUID.fromString(rs.getString("appointmentId")))
                        .providerId(UUID.fromString(rs.getString("providerId")))
                        .appointmentDate(rs.getDate("appointmentDate").toLocalDate())
                        .startTime(rs.getTime("startTime").toLocalTime())
                        .endTime(rs.getTime("endTime").toLocalTime())
                        .build());
            }
        } catch (SQLException sqlException) {
            log.error("Error retrieving the requested appointments. {}", (Object) sqlException.getStackTrace());
            return List.of();
        }
        return appointments;
    }

}
