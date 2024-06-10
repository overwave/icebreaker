--liquibase formatted sql

--changeset author:lizunya_navigation_request_create_table
CREATE TABLE IF NOT EXISTS navigation_request
(
    id              BIGSERIAL PRIMARY KEY,
    ship_id         BIGINT NOT NULL REFERENCES ship (id),
    start_point_id  BIGINT NOT NULL REFERENCES navigation_point(id),
    finish_point_id BIGINT NOT NULL REFERENCES navigation_point(id),
    start_date      TIMESTAMPTZ NOT NULL
);

--changeset author:lizunya_navigation_request_create_index
CREATE INDEX IF NOT EXISTS navigation_request_start_point_id_idx ON navigation_request (start_point_id);
CREATE INDEX IF NOT EXISTS navigation_request_finish_point_id_idx ON navigation_request (finish_point_id);
CREATE INDEX IF NOT EXISTS navigation_request_ship_id_idx ON navigation_request (ship_id);

--changeset author:lizunya_navigation_request_add_status
ALTER TABLE navigation_request
    ADD COLUMN IF NOT EXISTS status TEXT NOT NULL DEFAULT 'PENDING';