package dev.overwave.icebreaker.graph;

import dev.overwave.icebreaker.core.geospatial.Point;
import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.Map.Entry;

@UtilityClass
public class Mercator {
    private static final double EPS = 0.01;

    public Entry<Double, Double> pointToMercatorNormalized(Point point) {
        // double p = Math.pow(2, zoom + 8) / 2;
        double p = 0.5;

        double beta = Math.PI * point.lat() / 180;
        double phi = (1 - Math.sin(beta) * EPS) / (1 + Math.sin(beta) * EPS);
        double theta = Math.tan(Math.PI / 4 + beta / 2) * Math.pow(phi, EPS / 2);

        double x = p * (1 + point.lon() / 180);
        double y = p * (1 - Math.log(theta) / Math.PI);

        return Map.entry(x, y);
    }
}
