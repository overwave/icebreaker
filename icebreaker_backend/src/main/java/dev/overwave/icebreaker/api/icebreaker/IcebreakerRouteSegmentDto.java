package dev.overwave.icebreaker.api.icebreaker;

import dev.overwave.icebreaker.api.navigation.PointAndTimestamp;
import lombok.Builder;

import java.util.List;

@Builder
public record IcebreakerRouteSegmentDto(
        long id,
        boolean isParking,
        List<PointAndTimestamp> routes,
        boolean ships
) {
}
