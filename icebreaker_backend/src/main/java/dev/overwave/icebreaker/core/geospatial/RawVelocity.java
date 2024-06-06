package dev.overwave.icebreaker.core.geospatial;

import java.util.List;

public record RawVelocity(
        Point coordinates,
        List<ContinuousVelocity> velocities
) {
}
