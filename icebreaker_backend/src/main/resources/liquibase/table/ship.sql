--liquibase formatted sql

--changeset author:lizunya ship_create_table
CREATE TABLE IF NOT EXISTS ship
(
    id              BIGSERIAL PRIMARY KEY,
    name            TEXT NOT NULL,
    ice_class       TEXT NOT NULL,
    speed           FLOAT NOT NULL,
    icebreaker      BOOLEAN NOT NULL
    );

--changeset author:overwave_ship_speed_not_double
ALTER TABLE ship
    ALTER COLUMN speed TYPE FLOAT4;

--changeset author:overwave_ship_add_user_id
ALTER TABLE ship
    ADD COLUMN IF NOT EXISTS user_id BIGINT NOT NULL REFERENCES user_ (id) DEFAULT -1;
