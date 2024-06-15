package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.geospatial.Point;

public record PointAndTimestamp(
        Point point,
        long time
) {
}
