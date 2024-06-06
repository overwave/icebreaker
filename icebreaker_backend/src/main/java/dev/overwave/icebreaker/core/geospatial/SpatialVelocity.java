package dev.overwave.icebreaker.core.geospatial;

import dev.overwave.icebreaker.core.util.MathFunction;

import java.util.List;

public record SpatialVelocity(
        Point topLeft,
        Point topRight,
        Point bottomLeft,
        Point bottomRight,
        List<ContinuousVelocity> velocities
) {

    public boolean containsPoint(Point point) {
        //вершины передаются в порядке прохождения по ним по часовой стрелке
        List<Point> vertexes = List.of(topLeft, topRight, bottomRight, bottomLeft);
        int vertexNum = vertexes.size();
        return MathFunction.isPolygonContainsPoint(vertexes, point, vertexNum);
    }
}
