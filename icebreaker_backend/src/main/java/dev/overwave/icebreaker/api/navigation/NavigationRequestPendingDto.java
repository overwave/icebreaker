package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.RequestStatus;

import java.time.LocalDate;

public record NavigationRequestPendingDto(
        long id,
        RequestStatus status,
        long shipId,
        String shipName,
        //"Arc 4, 14 узлов"
        String shipClass,
        LocalDate startDate,
        long startPointId,
        String startPointName,
        long finishPointId,
        String finishPointName
) {
}
