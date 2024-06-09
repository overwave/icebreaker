package dev.overwave.icebreaker.core.route;

import dev.overwave.icebreaker.core.geospatial.ContinuousVelocity;
import dev.overwave.icebreaker.core.geospatial.Edge;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.graph.SparseList;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.ship.Ship;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
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

    public Optional<Route> createRoute(Point pointFrom, Point pointTo, Instant startDate, Graph graph, Ship ship) {
        List<Node> closestNodes = findClosestNodes(graph.getGraph(), pointFrom, pointTo);
        Node from = closestNodes.getFirst();
        Node to = closestNodes.getLast();

        PriorityQueue<Entry<Node, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Entry::getValue));
        queue.add(Map.entry(from, 0));

        Map<Node, RouteSegment> routeSegments = new HashMap<>();
        routeSegments.put(from, new RouteSegment(null, 0, ship.getSpeed()));

        while (!queue.isEmpty()) {
            Node current = queue.poll().getKey();
            if (current.coordinates().equals(to.coordinates())) {
                return Optional.of(buildRoute(startDate, routeSegments, current));
            }
            for (Edge nextEdge : current.edges()) {
                Instant currentTime = startDate.plus(routeSegments.get(current).durationMinutes(), ChronoUnit.MINUTES);
                float currentVelocity = getCurrentVelocity(currentTime, nextEdge.velocities());
                Entry<MovementType, Float> characteristics = getIceCharacteristics(ship, currentVelocity);
                if (characteristics.getKey() == MovementType.FORBIDDEN) {
                    continue;
                }
                float speedMpm = characteristics.getValue() * KNOTS_TO_METER_PER_MINUTES;
                int edgeTravelTime = (int) (nextEdge.distance() / speedMpm);
                int segmentDuration = routeSegments.get(current).durationMinutes() + edgeTravelTime;
                Node nextNode = nextEdge.getOther(current);
                RouteSegment nextSegment = routeSegments.get(nextNode);
                if (nextSegment == null || nextSegment.durationMinutes() > segmentDuration) {
                    int remainingTravelTime =
                            (int) (GraphFactory.getDistance(current.coordinates(), to.coordinates()) / speedMpm);
                    queue.add(Map.entry(nextNode, segmentDuration + remainingTravelTime));
                    routeSegments.put(nextNode, new RouteSegment(current, segmentDuration, speedMpm));
                }
            }
        }
        return Optional.empty();
    }

    private static float getCurrentVelocity(Instant currentTime, List<ContinuousVelocity> velocities) {
        for (ContinuousVelocity continuousVelocity : velocities) {
            if (continuousVelocity.interval().contains(currentTime)) {
                return continuousVelocity.velocity();
            }
        }
        List<ContinuousVelocity> sorted = velocities.stream()
                .sorted(Comparator.comparing(continuousVelocity -> continuousVelocity.interval().instant()))
                .toList();
        ContinuousVelocity first = sorted.getFirst();
        if (first.interval().instant().isAfter(currentTime)) {
            return first.velocity();
        } else {
            return sorted.getLast().velocity();
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
                distance += GraphFactory.getDistance(cursor.coordinates(), previous.coordinates());
            }
            route.add(cursor);
            cursor = previous;
        }
        return new Route(new Interval(startDate, Duration.ofMinutes(timeInMinutes)), route.reversed(), distance);
    }

    private static Entry<MovementType, Float> getIceCharacteristics(Ship ship, float integralVelocity) {
        return ship.getIceClass().getGroup().getCharacteristics(integralVelocity, ship.getSpeed());
    }

    private List<Node> findClosestNodes(List<SparseList<Node>> graphList, Point... points) {
        List<Node> result = Arrays.asList(new Node[points.length]);
        float[] minDistance = new float[points.length];
        Arrays.fill(minDistance, Float.MAX_VALUE);

        for (SparseList<Node> sparseList : graphList) {
            for (Node node : sparseList.getContent()) {
                for (int i = 0; i < points.length; i++) {
                    float currentDistance = GraphFactory.getDistance(points[i], node.coordinates());
                    if (currentDistance < minDistance[i]) {
                        minDistance[i] = currentDistance;
                        result.set(i, node);
                    }
                }
            }
        }
        return result;
    }
}
