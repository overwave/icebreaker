package dev.overwave.icebreaker.core.util;

import java.util.List;

public record RawVelocity(
        Point coordinates,
        List<ContinuousVelocity> velocities
) {
}
