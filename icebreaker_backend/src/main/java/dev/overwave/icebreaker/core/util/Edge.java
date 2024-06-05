package dev.overwave.icebreaker.core.util;

public record Edge(
        Node[] nodes,
        float distance,
        ContinuousVelocity[] velocities
) {
}
