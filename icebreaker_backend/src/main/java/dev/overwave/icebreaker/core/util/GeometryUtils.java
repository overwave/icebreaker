package dev.overwave.icebreaker.core.util;

import dev.overwave.icebreaker.core.geospatial.Point;

import java.awt.*;
import java.util.List;

public class GeometryUtils {
    public final static int ROUNDING = 1_000_000;

    public static boolean polygonContains(List<Point> vertices, Point point) {
        int vertexNum = vertices.size();
        int[] xCoordinates = new int[vertexNum];
        int[] yCoordinates = new int[vertexNum];
        for (int i = 0; i < vertexNum; i++) {
            //предположила что оси x соответствует долгота (longitude)
            // оси y - широта (latitude)
            xCoordinates[i] = (int) (vertices.get(i).lon() * ROUNDING);
            yCoordinates[i] = (int) (vertices.get(i).lat() * ROUNDING);
        }
        int x = (int) (point.lon() * ROUNDING);
        int y = (int) (point.lat() * ROUNDING);

        Polygon polygon = new Polygon(xCoordinates, yCoordinates, vertexNum);
        return polygon.contains(x, y);
    }
}
