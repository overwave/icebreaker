package dev.overwave.icebreaker.api.navigation;

import java.time.Instant;

public record VelocityIntervalDto(
        Instant start,
        Instant end
) {
}
