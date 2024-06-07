--liquibase formatted sql

--changeset author:lizunya_reference_point_create_table
CREATE TABLE IF NOT EXISTS reference_point
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    lat      FLOAT NOT NULL,
    lon      FLOAT NOT NULL
)