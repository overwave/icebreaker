package dev.overwave.icebreaker.core.ship;

import dev.overwave.icebreaker.core.route.DefaultRouteService;
import lombok.Builder;

import java.util.Objects;

@Builder
public record ShipStatic(
        long id,
        String name,
        IceClass iceClass,
        float speed,
        boolean icebreaker
) {
    public float getRelativeSpeed() {
        return speed / DefaultRouteService.REFERENCE_SPEED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((ShipStatic) o).id;
    }

    public int hashCode() {
        return Objects.hash(id);
    }
}
