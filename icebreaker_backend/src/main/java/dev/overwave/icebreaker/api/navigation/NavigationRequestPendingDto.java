package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.RequestStatus;

import java.time.LocalDate;

public record NavigationRequestPendingDto(
        long id,
        RequestStatus status,
        long shipId,
        String shipName,
        String shipClass,
        float speed,
        LocalDate startDate,
        long startPointId,
        String startPointName,
        long finishPointId,
        String finishPointName
) {
}
