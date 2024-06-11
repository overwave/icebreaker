--liquibase formatted sql

--changeset author:lizunya_velocity_interval_create_table
CREATE TABLE IF NOT EXISTS velocity_interval
(
    id            BIGSERIAL PRIMARY KEY,
    start_date    TIMESTAMPTZ NOT NULL,
    end_date      TIMESTAMPTZ NOT NULL

    );