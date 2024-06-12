package dev.overwave.icebreaker.core.graph;

import dev.overwave.icebreaker.core.geospatial.Node;

import java.util.List;

public record Graph(List<SparseList<Node>> graph) {
}
