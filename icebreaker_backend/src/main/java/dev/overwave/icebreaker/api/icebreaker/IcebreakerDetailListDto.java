package dev.overwave.icebreaker.api.icebreaker;

import java.util.List;

public record IcebreakerDetailListDto(
        List<IcebreakerDetailDto> icebreakers
) {
}
