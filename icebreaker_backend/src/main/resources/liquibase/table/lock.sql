--liquibase formatted sql

--changeset overwave:lock_table
CREATE TABLE IF NOT EXISTS lock
(
    id         BIGSERIAL PRIMARY KEY,
    status     TEXT        NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL
);
