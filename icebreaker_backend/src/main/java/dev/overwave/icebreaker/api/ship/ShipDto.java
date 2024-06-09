package dev.overwave.icebreaker.api.ship;

import dev.overwave.icebreaker.core.ship.IceClass;

public record ShipDto(
        long id,
        String name,
        float speed,
        IceClass iceClass
) {
}
