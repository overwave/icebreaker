package dev.overwave.icebreaker.core.geospatial;

import java.time.Duration;
import java.time.Instant;


public record Interval(Instant instant, Duration duration) {
}
