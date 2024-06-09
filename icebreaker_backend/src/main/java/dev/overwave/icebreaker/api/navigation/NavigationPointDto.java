package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.geospatial.Point;

public record NavigationPointDto(
        long id,
        String name,
        Point point
) {
}
