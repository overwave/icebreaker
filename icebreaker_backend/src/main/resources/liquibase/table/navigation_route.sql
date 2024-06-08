--liquibase formatted sql

--changeset author:overwave_navigation_route_create_table
CREATE TABLE IF NOT EXISTS navigation_route
(
    id           BIGSERIAL PRIMARY KEY,
    point_id_1   BIGINT REFERENCES navigation_point (id),
    point_id_2   BIGINT REFERENCES navigation_point (id),
    raw_distance FLOAT4
);

CREATE UNIQUE INDEX IF NOT EXISTS navigation_route_uniq_points_id_idx ON navigation_route (point_id_1, point_id_2);