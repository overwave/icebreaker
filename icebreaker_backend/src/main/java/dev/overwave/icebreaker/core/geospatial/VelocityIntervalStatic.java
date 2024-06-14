package dev.overwave.icebreaker.core.geospatial;

import lombok.Builder;

@Builder
public record VelocityIntervalStatic(
        long id,
        Interval interval
) {
}
