package dev.overwave.icebreaker.api.navigation;

import java.util.List;

public record NavigationRequestWithRouteDto(
        long id,
        long shipId,
        String shipName,
        String shipClass,
        boolean convoy,
        List<RouteSegmentDto> routes
) {
}
