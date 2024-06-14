package dev.overwave.icebreaker.api.navigation;

import java.time.LocalDate;

public record NavigationRequestToSaveDto(
        long shipId,
        long startPointId,
        long finishPointId,
        LocalDate startDate
) {
}
