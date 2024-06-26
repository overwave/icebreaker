package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.core.util.GeometryUtils;
import lombok.Builder;

import java.util.List;

@Builder
public record SpatialVelocity(
        Point topLeft,
        Point topRight,
        Point bottomLeft,
        Point bottomRight,
        List<ContinuousVelocity> velocities
) {

    public boolean containsPoint(Point point) {
        // вершины передаются в порядке прохождения по ним по часовой стрелке
        List<Point> vertices = List.of(topLeft, topRight, bottomRight, bottomLeft);
        return GeometryUtils.polygonContains(vertices, point);
    }

    public float getMinLat() {
        return Math.min(
                Math.min(topLeft.lat(), bottomLeft.lat()),
                Math.min(topRight.lat(), bottomRight.lat())
        );
    }
}
