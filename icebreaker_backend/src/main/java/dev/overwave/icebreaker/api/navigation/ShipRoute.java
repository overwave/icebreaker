package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.schedule.ConfirmedRouteSegment;
import dev.overwave.icebreaker.core.ship.ShipStatic;

import java.util.List;

public record ShipRoute(
        ShipStatic ship,
        List<ConfirmedRouteSegment> segments
) {
}
