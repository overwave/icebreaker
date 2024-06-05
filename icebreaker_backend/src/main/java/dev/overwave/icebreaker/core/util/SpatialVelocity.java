package dev.overwave.icebreaker.core.util;

public record SpatialVelocity(
        Point topLeft,
        Point topRight,
        Point bottomLeft,
        Point bottomRight,
        ContinuousVelocity[] velocities
) {
}
