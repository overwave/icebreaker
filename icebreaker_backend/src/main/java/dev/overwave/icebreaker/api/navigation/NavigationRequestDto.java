package dev.overwave.icebreaker.api.navigation;

import java.time.Instant;

public record NavigationRequestDto (
        long shipId,
        long startPointId,
        long finishPointId,
        Instant startDate
) {
}
