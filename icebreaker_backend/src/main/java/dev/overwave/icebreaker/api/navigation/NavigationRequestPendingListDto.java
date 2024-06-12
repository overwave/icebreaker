package dev.overwave.icebreaker.api.navigation;

import java.util.List;

public record NavigationRequestPendingListDto(
        List<NavigationRequestPendingDto> pending
) {
}
