package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.VelocityIntervalStatic;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRequestStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRouteStatic;
import dev.overwave.icebreaker.core.route.DefaultRouteStatic;
import dev.overwave.icebreaker.core.ship.ShipStatic;

import java.util.List;
import java.util.Map;

public record MetaRouteContext(
        Map<Long, ShipStatic> ships,
        Map<Long, NavigationRequestStatic> requests,
        Map<Long, NavigationPointStatic> points,
        Map<Long, NavigationRouteStatic> routes,
        Map<Long, List<DefaultRouteStatic>> defaultRoutesByRouteId,
        Map<Long, VelocityIntervalStatic> velocities
) {
}
