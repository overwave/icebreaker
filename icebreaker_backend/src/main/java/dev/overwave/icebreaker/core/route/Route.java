package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;

import java.util.List;

public record Route (
        Interval interval,
        List<Node> nodes,
        float distance
) {
}
