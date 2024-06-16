package dev.overwave.icebreaker.api.navigation;

import java.util.List;

public record NavigationRequestsDtoWithRoute(
        List<NavigationRequestPendingDto> pending,
        List<NavigationRequestWithRouteDto> agreed,
        List<NavigationRequestPendingDto> archive
) {
}
