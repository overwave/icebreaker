package dev.overwave.icebreaker.core.geospatial;

import java.time.Duration;
import java.time.Instant;


public record Interval(Instant instant, Duration duration) {

    public boolean contains(Instant instant) {
        Instant start = this.instant;
        Instant end = start.plus(this.duration);
        return (start.isBefore(instant) || start.equals(instant)) && end.isAfter(instant);
    }
}
