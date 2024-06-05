package dev.overwave.icebreaker.core.geospatial;

import java.util.List;
import java.util.Map.Entry;

public record Edge(
        Entry<Node, Node> nodes,
        float distance,
        List<ContinuousVelocity> velocities
) {
    @Override
    public String toString() {
        return "Edge{" +
               "distance=" + distance +
               ", velocities=" + velocities +
               '}';
    }
}
