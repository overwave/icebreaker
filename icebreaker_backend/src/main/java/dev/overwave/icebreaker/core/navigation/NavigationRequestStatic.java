package dev.overwave.icebreaker.core.navigation;

import lombok.Builder;

import java.time.Instant;
import java.util.Objects;

@Builder
public record NavigationRequestStatic(
        long id,
        long shipId,
        long startPointId,
        long finishPointId,
        Instant startDate) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((NavigationRequestStatic) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
