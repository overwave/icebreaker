--liquibase formatted sql

--changeset author:lizunya_user_create_table
CREATE TABLE IF NOT EXISTS user_
(
    id       BIGSERIAL PRIMARY KEY,
    name     TEXT NOT NULL,
    login    TEXT NOT NULL,
    password TEXT NOT NULL
);