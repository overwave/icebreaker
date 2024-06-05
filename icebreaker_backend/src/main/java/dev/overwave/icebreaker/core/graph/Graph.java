package dev.overwave.icebreaker.core.graph;

import dev.overwave.icebreaker.core.geospatial.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Graph {
    @Getter // temporary?
    private final List<SparseList<Node>> graph;
}
