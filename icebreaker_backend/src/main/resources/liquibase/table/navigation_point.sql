--liquibase formatted sql

--changeset author:lizunya_navigation_point_create_table
CREATE TABLE IF NOT EXISTS navigation_point
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    lat      FLOAT NOT NULL,
    lon      FLOAT NOT NULL
);