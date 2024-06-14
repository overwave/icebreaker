package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.ship.IceClassGroup;
import lombok.Builder;

import java.time.Duration;
import java.util.Objects;

@Builder
public record DefaultRouteStatic(
        long id,
        long routeId,
        Interval interval,
        IceClassGroup iceClassGroup,
        MovementType movementType,
        Duration travelTime
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((DefaultRouteStatic) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
