--liquibase formatted sql

--changeset author:lizunya_default_route_create_table
CREATE TABLE IF NOT EXISTS default_route
(
    id                   BIGSERIAL PRIMARY KEY,
    navigation_route_id  BIGINT REFERENCES navigation_route (id),
    velocity_interval_id BIGINT REFERENCES velocity_interval (id),
    ice_class            TEXT    NOT NULL,
    travel_time          BIGINT  NOT NULL,
    distance             FLOAT4  NOT NULL,
    possible             BOOLEAN NOT NULL,
    nodes                JSON    NOT NULL
);

--changeset author:lizunya_default_route_create_index
CREATE INDEX IF NOT EXISTS default_route_navigation_route_id_idx ON default_route (navigation_route_id);
CREATE INDEX IF NOT EXISTS default_route_velocity_interval_id_idx ON default_route (velocity_interval_id);

--changeset author:lizunya_default_route_alter_column_nodes
ALTER TABLE default_route
    ALTER COLUMN nodes TYPE TEXT;

--changeset author:lizunya_default_route_alter_column_ice_class
ALTER TABLE default_route
    RENAME COLUMN ice_class TO ice_group;

--changeset author:lizunya_default_route_add_column_movement_type
ALTER TABLE default_route
ADD COLUMN IF NOT EXISTS movement_type TEXT NOT NULL DEFAULT 'FORBIDDEN';


