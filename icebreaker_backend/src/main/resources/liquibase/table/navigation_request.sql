--liquibase formatted sql

--changeset author:lizunya_navigation_request_create_table
CREATE TABLE IF NOT EXISTS navigation_request
(
    id              BIGSERIAL PRIMARY KEY,
    ship_id         BIGINT REFERENCES ship (id),
    start_point_id  BIGINT REFERENCES reference_point (id),
    finish_point_id BIGINT REFERENCES reference_point (id),
    start_date      TIMESTAMPTZ NOT NULL
)