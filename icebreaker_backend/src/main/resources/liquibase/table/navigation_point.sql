--liquibase formatted sql

--changeset author:lizunya_navigation_point_create_table
CREATE TABLE IF NOT EXISTS navigation_point
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    lat      FLOAT NOT NULL,
    lon      FLOAT NOT NULL
);

--changeset author:overwave_navigation_point_add_external_id
ALTER TABLE navigation_point
    ADD COLUMN IF NOT EXISTS external_id INT NOT NULL DEFAULT -1 UNIQUE;

--changeset author:overwave_navigation_point_lat_lon_not_double
ALTER TABLE navigation_point
    ALTER COLUMN lat TYPE FLOAT4,
    ALTER COLUMN lon TYPE FLOAT4;
