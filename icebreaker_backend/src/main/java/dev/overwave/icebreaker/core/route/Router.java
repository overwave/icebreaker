package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Edge;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.SparseList;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import dev.overwave.icebreaker.core.util.GeometryUtils;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;

@UtilityClass
public class Router {

    public static final float KNOTS_TO_METER_PER_MINUTES = 1852F / 60F;
    public static final int ONE_HOUR_IN_MIN = (int) Duration.ofHours(1).toMinutes();

    public Optional<Route> createRoute(Node from, Node to, Instant startDate, ShipStatic ship,
                                       MovementType movementType, Duration referenceTime) {
        PriorityQueue<Entry<Node, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Entry::getValue));
        queue.add(Map.entry(from, 0));

        Map<Node, RouteSegment> routeSegments = new HashMap<>();
        routeSegments.put(from, new RouteSegment(null, 0, ship.speed()));

        while (!queue.isEmpty()) {
            Node current = queue.poll().getKey();
            if (current.coordinates().equals(to.coordinates())) {
                Route route = buildRoute(startDate, routeSegments, current);
                Route normalizedRoute = new Route(
                        route.interval(),
                        route.nodes(),
                        route.distance(),
                        normalizeRoutePoints(route.nodes(), routeSegments));
                return Optional.of(normalizedRoute);
            }
            for (Edge nextEdge : current.edges()) {
                int currentTravelDuration = routeSegments.get(current).durationMinutes();
                // если текущая длительность сформированного пути в 2 раза больше, чем полный путь под проводкой
                // ледокола - значит такой путь нам уже не подходит
                if (!referenceTime.isZero() && currentTravelDuration > referenceTime.toMinutes() * 2) {
                    return Optional.empty();
                }
                Instant currentTime = startDate.plus(currentTravelDuration, ChronoUnit.MINUTES);
                float currentVelocity = getCurrentVelocity(currentTime, nextEdge.velocities());
                Entry<MovementType, Float> characteristics = getIceCharacteristics(ship, currentVelocity, movementType);
                if (characteristics.getKey() == MovementType.FORBIDDEN) {
                    continue;
                }
                float speedMpm = characteristics.getValue() * KNOTS_TO_METER_PER_MINUTES;
                int edgeTravelTime = (int) (nextEdge.distance() / speedMpm);
                int segmentDuration = currentTravelDuration + edgeTravelTime;
                Node nextNode = nextEdge.getOther(current);
                RouteSegment nextSegment = routeSegments.get(nextNode);
                if (nextSegment == null || nextSegment.durationMinutes() > segmentDuration) {
                    int remainingTravelTime =
                            (int) (GeometryUtils.getDistance(current.coordinates(), to.coordinates()) / speedMpm);
                    queue.add(Map.entry(nextNode, segmentDuration + remainingTravelTime));
                    routeSegments.put(nextNode, new RouteSegment(current, segmentDuration, speedMpm));
                }
            }
        }
        return Optional.empty();
    }

    private static List<Point> normalizeRoutePoints(List<Node> nodes, Map<Node, RouteSegment> routeSegments) {
        List<Point> normalizedPoints = new ArrayList<>();
        Point from = nodes.getFirst().coordinates();
        long time = routeSegments.get(nodes.getFirst()).durationMinutes() - ONE_HOUR_IN_MIN;

        for (int i = 0, nodesSize = nodes.size(); i < nodesSize; ) {
            Node node = nodes.get(i);
            RouteSegment routeSegment = routeSegments.get(node);
            if (routeSegment.durationMinutes() > time + ONE_HOUR_IN_MIN) {
                float difference = routeSegment.durationMinutes() - time;
                Point point = GeometryUtils.findPointInPartOfSegment(from, node.coordinates(),
                         ONE_HOUR_IN_MIN / difference);
                from = point;
                normalizedPoints.add(point);
                time += ONE_HOUR_IN_MIN;
            } else if (routeSegment.durationMinutes() == time + ONE_HOUR_IN_MIN) {
                normalizedPoints.add(node.coordinates());
                from = node.coordinates();
                i++;
                time += ONE_HOUR_IN_MIN;
            } else {
                from = node.coordinates();
                i++;
            }
        }
        Point lastPoint = nodes.getLast().coordinates();
        if(!normalizedPoints.getLast().equals(lastPoint)) {
            normalizedPoints.add(lastPoint);
        }
        return normalizedPoints;
    }


    private static float getCurrentVelocity(Instant currentTime, List<ContinuousVelocity> velocities) {
        for (ContinuousVelocity continuousVelocity : velocities) {
            if (continuousVelocity.interval().contains(currentTime)) {
                return continuousVelocity.velocity();
            }
        }
        ContinuousVelocity first = velocities.getFirst();
        if (first.interval().instant().isAfter(currentTime)) {
            return first.velocity();
        } else {
            return velocities.getLast().velocity();
        }
    }

    private Route buildRoute(Instant startDate, Map<Node, RouteSegment> routeSegments, Node node) {
        List<Node> route = new LinkedList<>();
        float distance = 0;
        int timeInMinutes = routeSegments.get(node).durationMinutes();
        Node cursor = node;
        while (cursor != null) {
            Node previous = routeSegments.get(cursor).previous();
            if (previous != null) {
                distance += GeometryUtils.getDistance(cursor.coordinates(), previous.coordinates());
            }
            route.add(cursor);
            cursor = previous;
        }
        return new Route(new Interval(startDate, Duration.ofMinutes(timeInMinutes)), route.reversed(), distance, null);
    }

    private static Entry<MovementType, Float> getIceCharacteristics(ShipStatic ship, float integralVelocity,
                                                                    MovementType movementType) {
        return ship.iceClass().getGroup().getCharacteristics(integralVelocity, ship.speed(), movementType);
    }

    public Map<Point, Node> findClosestNodes(Graph graph, Point... points) {
        if (true) {
            return findClosestNodesFast(graph, points);
        }
        List<Node> result = Arrays.asList(new Node[points.length]);
        float[] minDistance = new float[points.length];
        Arrays.fill(minDistance, Float.MAX_VALUE);

        List<SparseList<Node>> sparseLists = graph.graph();
        for (SparseList<Node> sparseList : sparseLists) {
            for (Node node : sparseList.getContent()) {
                for (int i = 0; i < points.length; i++) {
                    float currentDistance = GeometryUtils.getDistance(points[i], node.coordinates());
                    if (currentDistance < minDistance[i]) {
                        minDistance[i] = currentDistance;
                        result.set(i, node);
                    }
                }
            }
        }
        Map<Point, Node> closestNodes = new HashMap<>();
        for (int i = 0; i < points.length; i++) {
            closestNodes.put(points[i], result.get(i));
        }
        return closestNodes;
    }

    public Map<Point, Node> findClosestNodesFast(Graph graph, Point... points) {
        List<SparseList<Node>> sparseLists = graph.graph();
        List<Float> latitudes = sparseLists.stream()
                .map(sparseList -> sparseList.getContent().getFirst())
                .map(node -> node.coordinates().lat()).toList();
        List<Float> longitudes = sparseLists.getFirst().getContent().stream()
                .map(node -> node.coordinates().lon()).toList();

        Map<Point, Node> closestNodes = new HashMap<>();
        for (Point point : points) {
            int closestLongitudeIndex = findClosestIndex(point.lon(), longitudes);
            int closestLatitudesIndex = findClosestIndex(point.lat(), latitudes);
            Node node = sparseLists.get(closestLatitudesIndex).getClosestSparse(closestLongitudeIndex);
            closestNodes.put(point, node);
        }
        return closestNodes;
    }

    private int findClosestIndex(float value, List<Float> array) {
        if (value <= array.get(0)) {
            return 0;
        }
        if (value >= array.get(array.size() - 1)) {
            return array.size() - 1;
        }
        int result = Collections.binarySearch(array, value);
        if (result >= 0) {
            return result;
        }
        int insertionPoint = -result - 1;
        return (array.get(insertionPoint) - value) < (value - array.get(insertionPoint - 1)) ?
                insertionPoint : insertionPoint - 1;
    }
}
