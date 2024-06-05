package dev.overwave.icebreaker.core.util;

import lombok.Data;

import java.time.Duration;
import java.time.Instant;


public record Interval(Instant instant, Duration duration) {
}
