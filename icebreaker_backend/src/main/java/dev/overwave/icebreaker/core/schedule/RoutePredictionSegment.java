package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;

import java.time.Duration;

public record RoutePredictionSegment(
        NavigationPointStatic from,
        NavigationPointStatic to,
        boolean convoy,
        Interval interval
) {
}
