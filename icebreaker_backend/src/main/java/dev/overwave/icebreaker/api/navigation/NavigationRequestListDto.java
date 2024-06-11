package dev.overwave.icebreaker.api.navigation;

import java.util.List;

public record NavigationRequestListDto(
        List<NavigationRequestDto> requests
) {
}
