package dev.overwave.icebreaker.core.util;

import dev.overwave.icebreaker.core.geospatial.Point;

import java.awt.*;
import java.util.List;

public class MathFunction {
    private final static int ROUNDING = 1_000_000;

    public static boolean isPolygonContainsPoint(List<Point> vertexes, Point point, int vertexNum) {
        int[] xCoordinates = new int[vertexNum];
        int[] yCoordinates = new int[vertexNum];
        for (int i = 0; i < vertexNum; i++) {
            //предположила что оси x соответствует долгота (longitude)
            // оси y - широта (latitude)
            xCoordinates[i] = (int) (vertexes.get(i).lon() * ROUNDING);
            yCoordinates[i] = (int) (vertexes.get(i).lat() * ROUNDING);
        }
        int x = (int) (point.lon() * ROUNDING);
        int y = (int) (point.lat() * ROUNDING);

        Polygon polygon = new Polygon(xCoordinates, yCoordinates, vertexNum);
        return polygon.contains(x, y);
    }
}
