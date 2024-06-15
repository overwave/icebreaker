package dev.overwave.icebreaker.core.parser;

import java.time.LocalDate;

public record ScheduleSegment(
        String startPointName,
        String finishPointName,
        LocalDate startDate,
        LocalDate finishDate
) {

    public boolean contains(LocalDate date) {
        return !startDate.isAfter(date) && !finishDate.isBefore(date);
    }
}
