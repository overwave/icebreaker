package dev.overwave.icebreaker.api.ship;

import dev.overwave.icebreaker.core.ship.IceClass;

public record IceClassDto(
        IceClass name,
        String description
) {
}
