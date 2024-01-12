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
    public int[] addOrUpdateAppointments(final List<AppointmentSlot> appointments) {
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement("""
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
            return statement.executeBatch();
        } catch (SQLException sqlException) {
            log.error("Error inserting the appointments into the appointmentSlots.");
            return new int[0];
        }
    }
    @Override
    public List<AppointmentSlot> findAppointmentsForProviderOnDate(final UUID providerID, final LocalDate date) {
        final List<AppointmentSlot> appointments = new ArrayList<>();
        final String baseQuery = "SELECT * FROM APPOINTMENT_SLOT WHERE provider_id =? AND appointment_date=?";
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(baseQuery);
            statement.setString(1, providerID.toString());
            statement.setDate(2, Date.valueOf(date));
            final ResultSet rs = statement.executeQuery();

            // Map over to the AppointmentSlot format- I would find a better  way to do this, given team input and more time.
            while(rs.next()) {
                appointments.add(buildAppointmentFromResultSet(rs));
            }
        } catch (SQLException sqlException) {
            log.error("Error retrieving the requested appointments. {}", (Object) sqlException.getStackTrace());
            return List.of();
        }
        return appointments;
    }

    @Override
    public AppointmentSlot findAppointmentById(final UUID appointmentId) {
        AppointmentSlot result = null;
        final String baseSql = "SELECT * FROM APP_DEMO.APPOINTMENT_SLOT WHERE appointment_id=?";
        try(Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection()) {
            final PreparedStatement statement = connection.prepareStatement(baseSql);
            statement.setString(1, appointmentId.toString());
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                result = buildAppointmentFromResultSet(rs);
            }
        } catch (SQLException sqlException) {
            log.error("Error retrieving the requested appointment. {}", (Object) sqlException.getStackTrace());
            return null;
        }
        return result;
    }

    private AppointmentSlot buildAppointmentFromResultSet(final ResultSet rs) throws SQLException{
        return AppointmentSlot.builder()
                .appointmentId(UUID.fromString(rs.getString("appointment_id")))
                .providerId(UUID.fromString(rs.getString("provider_id")))
                .appointmentDate(rs.getDate("appointment_date").toLocalDate())
                .startTime(rs.getTime("start_time").toLocalTime())
                .endTime(rs.getTime("end_time").toLocalTime())
                .build();
    }

}
