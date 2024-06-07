package dev.overwave.icebreaker.core.util;

import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class GridIndexer {
    private final TreeMap<Float, List<SpatialVelocity>> index;

    public GridIndexer(List<SpatialVelocity> velocityGrid) {
        index = new TreeMap<>();
        for (SpatialVelocity velocity : velocityGrid) {
            index.computeIfAbsent(velocity.getMinLat(), lat -> new ArrayList<>()).add(velocity);
        }
    }

    public SpatialVelocity findContaining(Point point) {
        SortedMap<Float, List<SpatialVelocity>> tail = index.headMap(point.lat(), true).reversed();
        for (List<SpatialVelocity> sameLatVelocities : tail.values()) {
            for (SpatialVelocity velocity : sameLatVelocities) {
                if (velocity.containsPoint(point)) {
                    return velocity;
                }
            }
        }
        return null;
    }
}
