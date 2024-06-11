--liquibase formatted sql

--changeset author:lizunya_ship_route_create_table
CREATE TABLE IF NOT EXISTS ship_route
(
    id                      BIGSERIAL PRIMARY KEY,
    navigation_request_id   BIGINT NOT NULL REFERENCES navigation_request(id),
    start_point_id          BIGINT NOT NULL REFERENCES navigation_point(id),
    finish_point_id         BIGINT NOT NULL REFERENCES navigation_point(id),
    start_date              TIMESTAMPTZ NOT NULL,
    end_date                TIMESTAMPTZ NOT NULL
);

--changeset author:lizunya_ship_route_create_index
CREATE INDEX IF NOT EXISTS ship_route_navigation_request_id_idx ON ship_route(navigation_request_id);
CREATE INDEX IF NOT EXISTS ship_route_start_point_id_idx ON ship_route(start_point_id);
CREATE INDEX IF NOT EXISTS ship_route_finish_point_id_idx ON ship_route(finish_point_id);
