package dev.overwave.icebreaker.core.graph;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Node;

import java.util.List;

public record Graph(List<SparseList<Node>> graph) {

    public List<ContinuousVelocity> getFirstVelocities() {
        return graph().getFirst().getContent().stream()
                .filter(node -> !node.edges().isEmpty()).findFirst().orElseThrow()
                .edges().getFirst().velocities();
    }
}
