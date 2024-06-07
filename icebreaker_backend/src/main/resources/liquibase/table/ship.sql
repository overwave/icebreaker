--liquibase formatted sql

--changeset author:lizunya ship_create_table
CREATE TABLE IF NOT EXISTS ship
(
    id              BIGSERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    ice_class       TEXT NOT NULL,
    speed           FLOAT NOT NULL,
    is_icebreaker   BOOLEAN NOT NULL
    );