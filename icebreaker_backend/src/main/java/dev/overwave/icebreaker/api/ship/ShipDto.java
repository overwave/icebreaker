package dev.overwave.icebreaker.api.ship;

import lombok.Builder;

@Builder
public record ShipDto(
        long id,
        String name,
        float speed,
        String iceClass
) {
}
