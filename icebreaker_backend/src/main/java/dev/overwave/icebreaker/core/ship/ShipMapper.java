package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.api.ship.ShipDto;
import org.springframework.stereotype.Component;

@Component
public class ShipMapper {
    public ShipDto map(Ship ship) {
        return new ShipDto(ship.getId(), ship.getName(), ship.getSpeed(), ship.getIceClass().name());
    }

    public ShipStatic toShipStatic(Ship ship) {
        return ShipStatic.builder()
                .id(ship.getId())
                .name(ship.getName())
                .iceClass(ship.getIceClass())
                .speed(ship.getSpeed())
                .icebreaker(ship.isIcebreaker())
                .build();
    }
}
