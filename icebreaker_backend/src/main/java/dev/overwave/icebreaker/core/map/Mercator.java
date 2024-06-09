package dev.overwave.icebreaker.core.map;

import dev.overwave.icebreaker.core.geospatial.Point;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Map.Entry;

@UtilityClass
public class Mercator {
    private static final double EPS = 0.0818191908426D;

    public Entry<Integer, Integer> pointToMercator(Point point, int zoom) {
        Entry<Double, Double> normalized = pointToMercatorNormalized(point);
        int scale = 2 << zoom + 7; // zoom 0 -> 256, zoom 1 -> 512
        return Map.entry(
                (int) (normalized.getKey() * scale),
                (int) (normalized.getValue() * scale)
        );
    }

    public Entry<Double, Double> pointToMercatorNormalized(Point point) {
        double beta = Math.PI * point.lat() / 180;
        double phi = (1 - Math.sin(beta) * EPS) / (1 + Math.sin(beta) * EPS);
        double theta = Math.tan(Math.PI / 4 + beta / 2) * Math.pow(phi, EPS / 2);

        double x = (1 + point.lon() / 180) / 2;
        double y = (1 - Math.log(theta) / Math.PI) / 2;

        return Map.entry(x, y);
    }
}
