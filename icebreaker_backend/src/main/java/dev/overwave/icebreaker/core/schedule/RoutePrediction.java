package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRouteStatic;

import java.util.List;
import java.util.Map.Entry;

public record RoutePrediction(
//        List<NavigationPointStatic> points,
        List<RoutePredictionSegment> segments
//        ,
//        Interval interval
) {
}

