package dev.overwave.icebreaker.core.util;

import dev.overwave.icebreaker.core.geospatial.Point;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import net.sf.geographiclib.GeodesicMask;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class GeometryUtils {
    public final static int ROUNDING = 1_000_000;
    private static final boolean DISABLE_LRU = true;
    private static final LruCache<Map.Entry<Point, Point>, Float> DISTANCE_CACHE = new LruCache<>(10);

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

    public static float getDistance(Point from, Point to) {
        if (DISABLE_LRU) {
            GeodesicLine line = Geodesic.WGS84.InverseLine(from.lat(), from.lon(), to.lat(), to.lon(),
                    GeodesicMask.DISTANCE_IN | GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
            return (float) line.Distance();
        }
        Map.Entry<Point, Point> entry = Map.entry(from, to);
        Float cached = DISTANCE_CACHE.get(entry);
        if (cached != null) {
            return cached;
        }
        GeodesicLine line = Geodesic.WGS84.InverseLine(from.lat(), from.lon(), to.lat(), to.lon(),
                GeodesicMask.DISTANCE_IN | GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        float distance = (float) line.Distance();
        DISTANCE_CACHE.put(entry, distance);
        return distance;
    }

    public static Point findPointInPartOfSegment(Point point1, Point point2, float ratio) {
        GeodesicLine line = Geodesic.WGS84.InverseLine(point1.lat(), point1.lon(), point2.lat(), point2.lon(),
                GeodesicMask.DISTANCE_IN | GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        GeodesicData g = line.ArcPosition(line.Arc() * ratio, GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
        return new Point((float) g.lat2, (float) g.lon2);

    }
}
