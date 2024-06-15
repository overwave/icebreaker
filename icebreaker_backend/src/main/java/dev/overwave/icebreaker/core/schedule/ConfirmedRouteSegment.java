package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import lombok.Builder;

import java.util.List;

@Builder
public record ConfirmedRouteSegment(
        Interval interval,
        List<Point> points,
        NavigationPointStatic from,
        NavigationPointStatic to,
        List<ShipStatic> otherShips,
        boolean convoy
) {
}
