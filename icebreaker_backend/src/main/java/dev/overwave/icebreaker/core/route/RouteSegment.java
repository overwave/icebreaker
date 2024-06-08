package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.geospatial.Node;

public record RouteSegment(
        Node previous,
        int durationMinutes,
        float segmentSpeedMpm
) {
}
