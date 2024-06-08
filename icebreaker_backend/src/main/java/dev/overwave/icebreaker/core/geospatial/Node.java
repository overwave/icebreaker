package dev.overwave.icebreaker.core.geospatial;

import java.util.List;
import java.util.Objects;

public record Node(
        Point coordinates,
        List<Edge> edges
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return Objects.equals(coordinates, node.coordinates);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(coordinates);
    }
}
