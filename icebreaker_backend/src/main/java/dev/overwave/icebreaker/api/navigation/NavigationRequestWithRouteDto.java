package dev.overwave.icebreaker.api.navigation;

import lombok.Builder;

import java.util.List;

@Builder
public record NavigationRequestWithRouteDto(
        long id,
        long shipId,
        String shipName,
        String shipClass,
        boolean convoy,
        List<RouteSegmentDto> routes
) {
}
