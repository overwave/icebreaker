package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.core.geospatial.Point;
import lombok.Builder;

import java.util.List;
import java.util.Objects;

@Builder
public record NavigationPointStatic(
        long id,
        String name,
        Point point,
        List<Long> routeIds
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((NavigationPointStatic) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
