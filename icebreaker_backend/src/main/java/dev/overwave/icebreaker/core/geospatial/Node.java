package dev.overwave.icebreaker.core.geospatial;

import java.util.List;

public record Node(
        Point coordinates,
        List<Edge> edges
) {
}
