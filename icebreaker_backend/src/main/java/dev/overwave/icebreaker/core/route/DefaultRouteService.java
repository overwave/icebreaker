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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultRouteService {
    public final static float REFERENCE_SPEED = 20.0F;

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
        Map<Point, Node> pointNodeMap = Router.findClosestNodesFast(graph, points);
        Map<IceClassGroup, IceClass> iceClassByGroup = getIceClassByGroup();

        for (NavigationRoute edge : edges) {
            nextInterval:
            for (VelocityInterval interval : intervals) {
                // группы должны быть в порядке убывания по проходимости
                List<IceClassGroup> iceClassGroupsDescending = Arrays.asList(IceClassGroup.values()).reversed();
                for (IceClassGroup iceClassGroup : iceClassGroupsDescending) {
                    Ship ship = new Ship("ship", iceClassByGroup.get(iceClassGroup), REFERENCE_SPEED,
                            iceClassGroup.isIcebreaker(), null, null);
                    Point from = edge.getPoint1().getPoint();
                    Point to = edge.getPoint2().getPoint();
                    Optional<Route> routeFollowingO = Router.createRoute(
                            pointNodeMap.get(from),
                            pointNodeMap.get(to),
                            interval.getStartDate(),
                            ship,
                            MovementType.FOLLOWING,
                            Duration.ofDays(5));
                    if (routeFollowingO.isEmpty()) {
                        DefaultRoute defaultRouteImpossible = DefaultRoute.builder()
                                .edge(edge)
                                .iceClassGroup(iceClassGroup)
                                .velocityInterval(interval)
                                .travelTimeMinutes(Duration.ZERO.toMinutes())
                                .distance(Float.POSITIVE_INFINITY)
                                .possible(false)
                                .nodes("")
                                .movementType(MovementType.FORBIDDEN)
                                .build();
                        defaultRouteRepository.save(defaultRouteImpossible);
                        System.err.printf(
                                "%s Route from %s to %s at %s by %s impossible, skipping to next interval%n",
                                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                                edge.getPoint1().getName(),
                                edge.getPoint2().getName(),
                                interval.getStartDate().atOffset(ZoneOffset.UTC).toLocalDate(),
                                iceClassGroup.name()
                        );
                        continue nextInterval;
                    }
                    Route routeFollowing = routeFollowingO.get();
                    Duration travelTimeFollowing = routeFollowing.interval().duration();

                    Optional<Route> routeIndependentO = Router.createRoute(
                            pointNodeMap.get(from),
                            pointNodeMap.get(to),
                            interval.getStartDate(),
                            ship,
                            MovementType.INDEPENDENT,
                            travelTimeFollowing);
                    if (routeIndependentO.isPresent()) {
                        Route routeIndependent = routeIndependentO.get();
                        long travelTimeIndependent = routeIndependent.interval().duration().toMinutes();
                        DefaultRoute defaultRouteIndependent = DefaultRoute.builder()
                                .edge(edge)
                                .iceClassGroup(iceClassGroup)
                                .velocityInterval(interval)
                                .travelTimeMinutes(travelTimeIndependent)
                                .distance(routeIndependent.distance())
                                .possible(true)
                                .nodes(serializeNodes(routeIndependent.nodes()))
                                .movementType(MovementType.INDEPENDENT)
                                .build();
                        defaultRouteRepository.save(defaultRouteIndependent);
                        System.out.printf(
                                "%s Route from %s to %s at %s by %s independent%n",
                                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                                edge.getPoint1().getName(),
                                edge.getPoint2().getName(),
                                interval.getStartDate().atOffset(ZoneOffset.UTC).toLocalDate(),
                                iceClassGroup.name()
                        );
                    } else {
                        DefaultRoute defaultRouteFollowing = DefaultRoute.builder()
                                .edge(edge)
                                .iceClassGroup(iceClassGroup)
                                .velocityInterval(interval)
                                .travelTimeMinutes(travelTimeFollowing.toMinutes())
                                .distance(routeFollowing.distance())
                                .possible(true)
                                .nodes(serializeNodes(routeFollowing.nodes()))
                                .movementType(MovementType.FOLLOWING)
                                .build();
                        defaultRouteRepository.save(defaultRouteFollowing);
                        System.out.printf(
                                "%s Route from %s to %s at %s by %s following%n",
                                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                                edge.getPoint1().getName(),
                                edge.getPoint2().getName(),
                                interval.getStartDate().atOffset(ZoneOffset.UTC).toLocalDate(),
                                iceClassGroup.name()
                        );
                    }
                }
            }
        }

    }

    private Map<IceClassGroup, IceClass> getIceClassByGroup() {
        return Arrays.stream(IceClass.values())
                .collect(Collectors.toMap(IceClass::getGroup, it -> it, (a, b) -> b));
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
