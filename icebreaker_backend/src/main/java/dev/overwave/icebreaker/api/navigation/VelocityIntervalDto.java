package dev.overwave.icebreaker.api.navigation;

import java.time.Instant;

public record VelocityIntervalDto(
        long id,
        Instant startDate,
        Instant endDate
) {
}
