package dev.overwave.icebreaker.api.ship;

import java.util.List;

public record ShipListDto(
        List<ShipDto> ships
) {
}
