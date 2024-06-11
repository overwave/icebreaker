package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.RequestStatus;

import java.time.Instant;

public record NavigationRequestDto (
        long shipId,
        long startPointId,
        long finishPointId,
        Instant startDate,
        RequestStatus status
) {
}
