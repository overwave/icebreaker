package dev.overwave.icebreaker.api.dto_for_Balya;

import java.util.List;

public record NavigationRequestsDtoWithRoute(
        List<NavigationRequestDtoForBalya> pending,
        List<NavigationRequestWithRouteDto> agreed,
        List<NavigationRequestWithRouteDto> archive
) {
}
