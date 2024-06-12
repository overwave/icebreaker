package dev.overwave.icebreaker.core.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalRepository;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.navigation.NavigationPoint;
import dev.overwave.icebreaker.core.navigation.NavigationPointRepository;
import dev.overwave.icebreaker.core.navigation.NavigationRoute;
import dev.overwave.icebreaker.core.navigation.NavigationRouteRepository;
import dev.overwave.icebreaker.core.serialization.SerializationUtils;
import dev.overwave.icebreaker.core.ship.IceClass;
import dev.overwave.icebreaker.core.ship.IceClassGroup;
import dev.overwave.icebreaker.core.ship.Ship;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultRouteService {
    private final static float REFERENCE_SPEED = 20.0F;

    private final NavigationRouteRepository navigationRouteRepository;
    private final DefaultRouteRepository defaultRouteRepository;
    private final VelocityIntervalRepository velocityIntervalRepository;
    private final NavigationPointRepository navigationPointRepository;
    private final ObjectMapper objectMapper;

    public void createAllDefaultRoutes() {
        List<NavigationRoute> edges = navigationRouteRepository.findAll();
        List<VelocityInterval> intervals = velocityIntervalRepository.findAll();

        Graph graph = SerializationUtils.readWeightedGraph("data/graph.lz4");
        Point[] points = navigationPointRepository.findAll().stream()
                .map(NavigationPoint::getPoint)
                .toArray(Point[]::new);
        Map<Point, Node> pointNodeMap = Router.findClosestNodes(graph, points);
        Map<IceClassGroup, IceClass> iceClassByGroup = getIceClassByGroup();

        for (NavigationRoute edge : edges) {
            for (VelocityInterval interval : intervals) {
                for (IceClassGroup iceGroup : IceClassGroup.values()) {

                    Ship ship = new Ship("ship", iceClassByGroup.get(iceGroup), REFERENCE_SPEED,
                            iceGroup.isIcebreaker(), null, null);
                    Point from = edge.getPoint1().getPoint();
                    Point to = edge.getPoint2().getPoint();
                    Optional<Route> routeOFollowing = Router.createRoute(pointNodeMap.get(from), pointNodeMap.get(to),
                            interval.getStartDate(), graph, ship, MovementType.FOLLOWING, 0L);
                    if (routeOFollowing.isEmpty()) {
                        DefaultRoute defaultRouteImpossible = DefaultRoute.builder()
                                .edge(edge)
                                .iceGroup(iceGroup)
                                .velocityInterval(interval)
                                .travelTime(Duration.ZERO.toMinutes())
                                .distance(Float.POSITIVE_INFINITY)
                                .possible(false)
                                .nodes("")
                                .movementType(MovementType.FORBIDDEN)
                                .build();
                        defaultRouteRepository.save(defaultRouteImpossible);
                    } else {
                        Route routeFollowing = routeOFollowing.get();
                        long travelTimeFollowing = routeFollowing.interval().duration().toMinutes();
                        DefaultRoute defaultRouteFollowing = DefaultRoute.builder()
                                .edge(edge)
                                .iceGroup(iceGroup)
                                .velocityInterval(interval)
                                .travelTime(travelTimeFollowing)
                                .distance(routeFollowing.distance())
                                .possible(true)
                                .nodes(serializeNodes(routeFollowing.nodes()))
                                .movementType(MovementType.FOLLOWING)
                                .build();
                        defaultRouteRepository.save(defaultRouteFollowing);

                        Optional<Route> routeOIndependent = Router.createRoute(pointNodeMap.get(from),
                                pointNodeMap.get(to),
                                interval.getStartDate(),
                                graph,
                                ship, MovementType.INDEPENDENT, travelTimeFollowing);
                        if(routeOIndependent.isPresent()) {
                            Route routeIndependent = routeOIndependent.get();
                            long travelTimeIndependent = routeIndependent.interval().duration().toMinutes();
                            DefaultRoute defaultRouteIndependent = DefaultRoute.builder()
                                    .edge(edge)
                                    .iceGroup(iceGroup)
                                    .velocityInterval(interval)
                                    .travelTime(travelTimeIndependent)
                                    .distance(routeIndependent.distance())
                                    .possible(true)
                                    .nodes(serializeNodes(routeIndependent.nodes()))
                                    .movementType(MovementType.INDEPENDENT)
                                    .build();
                            defaultRouteRepository.save(defaultRouteIndependent);
                        }
                    }
                    if (true) {
                        return;
                    }
                }
            }
        }

    }

    private static Map<IceClassGroup, IceClass> getIceClassByGroup() {
        Map<IceClassGroup, IceClass> iceClassByGroup = new HashMap<>();
        iceClassByGroup.put(IceClassGroup.ICE_0_3, IceClass.ICE_2);
        iceClassByGroup.put(IceClassGroup.ARC_4_6, IceClass.ARC_5);
        iceClassByGroup.put(IceClassGroup.ARC_7_8, IceClass.ARC_7);
        iceClassByGroup.put(IceClassGroup.ARC_9_TAIMIR_VAIGACH, IceClass.ARC_9_TAIMIR_VAIGACH);
        iceClassByGroup.put(IceClassGroup.ARC_9_50_YEARS_OF_VICTORY_YAMAL, IceClass.ARC_9_50_YEARS_OF_VICTORY_YAMAL);
        return iceClassByGroup;
    }

    @SneakyThrows
    private String serializeNodes(Optional<Route> routeO) {
        List<Point> points = routeO.stream()
                .map(Route::nodes)
                .flatMap(List::stream)
                .map(Node::coordinates)
                .toList();
        return objectMapper.writeValueAsString(points);
    }

    @SneakyThrows
    private String serializeNodes(List<Node> nodes) {
        List<Point> points = nodes.stream()
                .map(Node::coordinates)
                .toList();
        return objectMapper.writeValueAsString(points);
    }
}
