--liquibase formatted sql

--changeset author:lizunya_user_create_table
CREATE TABLE IF NOT EXISTS user_
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    login    TEXT NOT NULL,
    password TEXT NOT NULL
);

--changeset author:overwave_user_role
ALTER TABLE user_
    ADD COLUMN IF NOT EXISTS roles TEXT[] NOT NULL DEFAULT ARRAY[]::TEXT[];

--changeset author:overwave_user_role_single
ALTER TABLE user_
    DROP COLUMN IF EXISTS roles,
    ADD COLUMN IF NOT EXISTS role TEXT;
