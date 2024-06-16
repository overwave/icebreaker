package dev.overwave.icebreaker.api.icebreaker;

import java.util.List;

public record IcebreakerRouteDto(
        List<IcebreakerRouteSegmentDto> segments
) {
}
