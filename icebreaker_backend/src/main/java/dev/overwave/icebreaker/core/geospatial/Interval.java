package dev.overwave.icebreaker.core.geospatial;

import java.time.Duration;
import java.time.Instant;


public record Interval(Instant instant, Duration duration) {

    public boolean contains(Instant other) {
        return (start().isBefore(other) || start().equals(other)) && end().isAfter(other);
    }

    public Instant start() {
        return instant;
    }

    public Instant end() {
        return instant.plus(duration);
    }

    public static Interval ofStartEnd(Instant start, Instant end) {
        return new Interval(start, Duration.between(start, end));
    }
}
