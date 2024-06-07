package dev.overwave.icebreaker.api.navigation;

import java.time.LocalDateTime;

public record NavigationRequestDto (
        long shipId,
        long startPointId,
        long finishPointId,
        LocalDateTime startDate
) {
}
