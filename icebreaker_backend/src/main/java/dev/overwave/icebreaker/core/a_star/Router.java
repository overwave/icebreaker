package dev.overwave.icebreaker.core.a_star;

import dev.overwave.icebreaker.core.geospatial.Edge;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.graph.GraphFactory;
import dev.overwave.icebreaker.core.graph.SparseList;
import dev.overwave.icebreaker.core.navigation.Ship;
import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.function.Supplier;
import java.util.stream.Stream;

@UtilityClass
public class Router {

    public static final float KNOTS_TO_METER_PER_MINUTES = 1852F / 60F;

    public Optional<Route> createRoute(Point pointFrom, Point pointTo, Instant startDate, Graph graph, Ship ship) {
        Supplier<Stream<Node>> graphList = () -> graph.getGraph().stream()
                .map(SparseList::getContent)
                .flatMap(List::stream);

        List<Node> closestNodes = findClosestNodes(graph.getGraph(), pointFrom, pointTo);
        Node from = closestNodes.getFirst();
        Node to = closestNodes.getLast();

        PriorityQueue<Entry<Node, Integer>> queue = new PriorityQueue<>(Comparator.comparingInt(Entry::getValue));
        queue.add(Map.entry(from, 0));

        Map<Node, Node> cameFrom = new HashMap<>();
        cameFrom.put(from, null);

        Map<Node, Integer> costSoFarMinutes = new HashMap<>();
        costSoFarMinutes.put(from, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll().getKey();
            if (current.coordinates().equals(to.coordinates())) {
                return Optional.of(buildRoute(cameFrom, current));
            }
            for (Edge nextEdge : current.edges()) {
                float speedMpm = getSpeedMpm(ship, nextEdge.velocities().getFirst().velocity());
                int edgeTravelTime = (int) (nextEdge.distance() / speedMpm);
                int newCost = costSoFarMinutes.get(current) + edgeTravelTime;
                Node nextNode = nextEdge.getOther(current);
                Integer nextNodeCost = costSoFarMinutes.get(nextNode);
                if (nextNodeCost == null || newCost < nextNodeCost) {
                    costSoFarMinutes.put(nextNode, newCost);
                    int remainingTravelTime =
                            (int) (GraphFactory.getDistance(current.coordinates(), to.coordinates()) / speedMpm);
                    queue.add(Map.entry(nextNode, newCost + remainingTravelTime));
                    cameFrom.put(nextNode, current);
                }
            }
        }
        return Optional.empty();
    }

    private float estimatedTravelTime(Node to, Node nextNode) {
        return GraphFactory.getDistance(to.coordinates(), nextNode.coordinates());
    }

    private Route buildRoute(Map<Node, Node> cameFrom, Node node) {
        List<Node> route = new LinkedList<>();
        Node cursor = node;
        while (cursor != null) {
            route.add(cursor);
            cursor = cameFrom.get(node);
        }
        return new Route(null, route.reversed(), 0);
    }

    private int getTravelMinutes(Edge edge, Ship ship) {
        return (int) (edge.distance() / getSpeedMpm(ship, edge.velocities().getFirst().velocity()));
    }

    private static float getSpeedMpm(Ship ship, float integralVelocity) {
        return ship.getIceClass().getCharacteristics(integralVelocity, ship.getSpeed()).getValue() *
                KNOTS_TO_METER_PER_MINUTES;
    }

    private List<Node> findClosestNodes(List<SparseList<Node>> graphList, Point... points) {
        List<Node> result = new ArrayList<>();
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
