package dev.overwave.icebreaker.api.navigation;

import dev.overwave.icebreaker.core.navigation.RequestStatus;
import jakarta.annotation.Nullable;

import java.time.LocalDate;

public record NavigationRequestDto(
        long shipId,
        long startPointId,
        long finishPointId,
        LocalDate startDate,
        @Nullable
        RequestStatus status
) {
}
