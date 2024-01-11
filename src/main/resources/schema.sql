CREATE SCHEMA IF NOT EXISTS APP_DEMO;
SET SCHEMA APP_DEMO;

CREATE TABLE IF NOT EXISTS RESERVATION(
    reservation_id VARCHAR(255) NOT NULL,
    appointment_id VARCHAR(255),
    client_name VARCHAR(255),
    confirmation_sent_at TIMESTAMP WITH TIME ZONE,
    confirmed BOOLEAN,
    PRIMARY KEY (reservation_id)
);

CREATE TABLE IF NOT EXISTS APPOINTMENT_SLOT(
    appointment_id VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255) NOT NULL,
    appointment_date DATE,
    start_time TIME,
    end_time TIME,
    PRIMARY KEY (appointment_id)
);

CREATE TABLE IF NOT EXISTS PROVIDERS(
    provider_id VARCHAR(255) NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (provider_id)
);