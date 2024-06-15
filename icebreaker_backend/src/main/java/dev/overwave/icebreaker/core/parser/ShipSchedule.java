package dev.overwave.icebreaker.core.parser;

import java.util.List;

public record ShipSchedule(
        String shipName,
        List<ScheduleSegment> segments
) {
}
