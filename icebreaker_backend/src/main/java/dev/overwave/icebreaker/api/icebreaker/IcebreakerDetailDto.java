package dev.overwave.icebreaker.api.icebreaker;

import dev.overwave.icebreaker.core.navigation.IcebreakerRouteSegment;
import lombok.Builder;

import java.util.List;

@Builder
public record IcebreakerDetailDto(
        long id,
        String name,
        String iceClass,
        float speed,
        List<IcebreakerRouteSegment> route
) {
}
