package dev.overwave.icebreaker.core.geospatial;

import java.util.List;

public record Edge(
        List<Node> nodes,
        float distance,
        List<ContinuousVelocity> velocities
) {
}
