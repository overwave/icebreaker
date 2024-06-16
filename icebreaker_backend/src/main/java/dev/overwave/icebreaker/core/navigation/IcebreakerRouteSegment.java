package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.ship.ShipDto;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record IcebreakerRouteSegment(
        long id,
        boolean isParking,
        LocalDate startDate,
        long startPointId,
        String startPointName,
        LocalDate finishDate,
        long finishPointId,
        String finishPointName,
        List<ShipDto> ships
) {
}
