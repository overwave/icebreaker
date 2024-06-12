package dev.overwave.icebreaker.api.dto_for_Balya;

import java.util.List;

public record NavigationRequestWithRouteDto(
        long id,
        long shipId,
        String shipName,
        String shipClass,
        boolean convoy,
        List<RouteDtoForBalya> routes
) {
}
