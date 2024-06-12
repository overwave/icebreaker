package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.RequestStatus;

import java.time.LocalDate;

public record NavigationRequestDto(
        long shipId,
        long startPointId,
        long finishPointId,
        LocalDate startDate,
        RequestStatus status
) {
}
