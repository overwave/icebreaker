package dev.overwave.icebreaker.core.graph;


import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Edge;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.SpatialVelocity;
import dev.overwave.icebreaker.core.util.GridIndexer;
import dev.overwave.icebreaker.core.util.ListUtils;
import dev.overwave.icebreaker.core.util.LruCache;
import lombok.experimental.UtilityClass;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicLine;
import net.sf.geographiclib.GeodesicMask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@UtilityClass
public class GraphFactory {
    public static final float MIN_LATITUDE = 64F;
    public static final float MAX_LATITUDE = 84F;
    public static final float MIN_LONGITUDE = 20F;
    public static final float MAX_LONGITUDE = 200F;
    private static final float BASE_EDGE_LENGTH = 10_000F; // 1 km
    private static final boolean DISABLE_LRU = true;

    private static final LruCache<Entry<Point, Point>, Float> DISTANCE_CACHE = new LruCache<>(10);

    public Graph buildWeightedGraph(List<SpatialVelocity> velocityGrid) {
        List<SparseList<Node>> uniformGraph = buildUniformGraph();
        GridIndexer gridIndexer = new GridIndexer(velocityGrid);
        connectNeighbours(uniformGraph, gridIndexer);
        return new Graph(uniformGraph);
    }

    private List<SparseList<Node>> buildUniformGraph() {
        Point cursor = new Point(MIN_LATITUDE, MIN_LONGITUDE);
        int sparseFactor = 0;
        float latStep = getLatStep(cursor);
        float lonStep = getLonStep(cursor);
        List<SparseList<Node>> grid = new ArrayList<>();

        while (cursor.lat() < MAX_LATITUDE) {
            List<Node> points = new ArrayList<>();
            while (cursor.lon() < MAX_LONGITUDE) {
                points.add(new Node(cursor, new ArrayList<>()));
                cursor = new Point(cursor.lat(), cursor.lon() + lonStep);
            }
            grid.add(new SparseList<>(sparseFactor, points));
            cursor = new Point(cursor.lat() + latStep, MIN_LONGITUDE);

            // latStep = getLatStep(cursor); // примерно одинаков
            if (getLonStep(cursor) > lonStep * 2) {
                sparseFactor++;
                lonStep *= 2;
            }
        }
        return grid;
    }

    private void connectNeighbours(List<SparseList<Node>> pointGraph, GridIndexer gridIndexer) {
        int width = pointGraph.getFirst().getContent().size();

        for (int row = 0; row < pointGraph.size(); row++) {
            SparseList<Node> points = pointGraph.get(row);
            SparseList<Node> topPoints = ListUtils.getOrDefault(pointGraph, row + 1, SparseList.empty());
            SparseList<Node> topTopPoints = ListUtils.getOrDefault(pointGraph, row + 2, SparseList.empty());

            int step = 1 << points.getSparseFactor();
            for (int col = 0; col < width; col += step) {
                Node current = points.getSparse(col);

                Node right = points.getSparse(col + step);
                Node rightRightTop = topPoints.getSparse(col + step + step);
                Node topRight = topPoints.getSparse(col + step);
                Node topTopRight = topTopPoints.getSparse(col + step);
                Node top = topPoints.getSparse(col);
                Node topTopLeft = topTopPoints.getSparse(col - step);
                Node topLeft = topPoints.getSparse(col - step);
                Node leftLeftTop = topPoints.getSparse(col - step - step);

                connect(current, right, gridIndexer);
                connect(current, rightRightTop, gridIndexer);
                connect(current, topRight, gridIndexer);
                connect(current, topTopRight, gridIndexer);
                connect(current, top, gridIndexer);
                connect(current, topTopLeft, gridIndexer);
                connect(current, topLeft, gridIndexer);
                connect(current, leftLeftTop, gridIndexer);
            }
        }
    }

    private void connect(Node first, Node second, GridIndexer gridIndexer) {
        if (first == null || second == null) {
            return;
        }
        Edge edge = new Edge(
                Map.entry(first, second),
                getDistance(first.coordinates(), second.coordinates()),
                getContinuousVelocities(first, second, gridIndexer)
        );
        first.edges().add(edge);
        second.edges().add(edge);
    }

    private List<ContinuousVelocity> getContinuousVelocities(Node first, Node second, GridIndexer gridIndexer) {
        Point midPoint = getMiddle(first.coordinates(), second.coordinates());
        SpatialVelocity spatialVelocity = gridIndexer.findContaining(midPoint);
        if (spatialVelocity == null) {
            return null;
        }
        return spatialVelocity.velocities();
    }

    private Point getMiddle(Point first, Point second) {
        return new Point((first.lat() + second.lat()) / 2, (first.lon() + second.lon()) / 2);
    }

    private float getLatStep(Point point) {
        float testStep = 0.001F;
        float distance = getDistance(point, new Point(point.lat() + testStep, point.lon()));
        return testStep * GraphFactory.BASE_EDGE_LENGTH / distance;
    }

    private float getLonStep(Point point) {
        float testStep = 0.001F;
        float distance = getDistance(point, new Point(point.lat(), point.lon() + testStep));
        return testStep * GraphFactory.BASE_EDGE_LENGTH / distance;
    }

    public float getDistance(Point from, Point to) {
        if (DISABLE_LRU) {
            GeodesicLine line = Geodesic.WGS84.InverseLine(from.lat(), from.lon(), to.lat(), to.lon(),
                    GeodesicMask.DISTANCE_IN | GeodesicMask.LATITUDE | GeodesicMask.LONGITUDE);
            return (float) line.Distance();
        }
        Entry<Point, Point> entry = Map.entry(from, to);
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

    public static void print() {
        Runtime runtime = Runtime.getRuntime();

        long allocated = runtime.totalMemory();
        long used = allocated - runtime.freeMemory();
        long max = runtime.maxMemory();

        long available = max - used;

        System.out.printf("Max: %d Allocated: %d Used: %d Available: %d%n",
                toMb(max),
                toMb(allocated),
                toMb(used),
                toMb(available));
    }

    private static long toMb(long bytes) {
        return bytes / 1_000_000;
    }
}
