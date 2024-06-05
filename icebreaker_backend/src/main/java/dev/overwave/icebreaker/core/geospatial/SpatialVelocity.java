package dev.overwave.icebreaker.core.geospatial;

import java.util.List;

public record SpatialVelocity(
        Point topLeft,
        Point topRight,
        Point bottomLeft,
        Point bottomRight,
        List<ContinuousVelocity> velocities
) {
}
