package dev.overwave.icebreaker.api.navigation;

import lombok.Builder;

import java.time.LocalDate;


@Builder
public record RouteSegmentDto(
        long id,
        LocalDate startDate,
        long startPointId,
        String startPointName,
        LocalDate finishDate,
        long finishPointId,
        String finishPointName,
        String icebreakerName,
        String icebreakerClass
) {
}
