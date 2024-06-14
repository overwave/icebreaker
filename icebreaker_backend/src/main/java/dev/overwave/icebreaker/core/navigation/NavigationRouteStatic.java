package dev.overwave.icebreaker.core.navigation;

import lombok.Builder;

import java.util.Map.Entry;
import java.util.Objects;

@Builder
public record NavigationRouteStatic(
        long id,
        Entry<Long, Long> pointIds
) {
    public long getOther(long pointId) {
        return pointIds.getKey().equals(pointId) ? pointIds.getValue() : pointIds.getKey();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id == ((NavigationRouteStatic) o).id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
