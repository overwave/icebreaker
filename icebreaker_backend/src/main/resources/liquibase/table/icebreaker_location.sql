--liquibase formatted sql

--changeset overwave:icebreaker_location_create_table
CREATE TABLE IF NOT EXISTS icebreaker_location
(
    id            BIGSERIAL PRIMARY KEY,
    icebreaker_id BIGINT      NOT NULL REFERENCES ship (id),
    point_id      BIGINT      NOT NULL REFERENCES navigation_point (id),
    start_date    TIMESTAMPTZ NOT NULL
);

--changeset overwave:icebreaker_location_create_index
CREATE INDEX IF NOT EXISTS icebreaker_location_point_id_idx ON icebreaker_location (point_id);
CREATE INDEX IF NOT EXISTS icebreaker_location_icebreaker_id_idx ON icebreaker_location (icebreaker_id);
