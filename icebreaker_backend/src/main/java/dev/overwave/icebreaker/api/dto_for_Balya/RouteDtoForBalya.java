package dev.overwave.icebreaker.api.dto_for_Balya;

import java.time.LocalDate;


public record RouteDtoForBalya(
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
