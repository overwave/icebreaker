package dev.overwave.icebreaker.api.ship;

import dev.overwave.icebreaker.core.ship.IceClass;

public record ShipCreateRequest(
        String name,
        float speed,
        IceClass iceClass
) {
}
