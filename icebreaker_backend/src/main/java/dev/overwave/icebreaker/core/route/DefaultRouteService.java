package dev.overwave.icebreaker.core.route;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalRepository;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.navigation.NavigationRoute;
import dev.overwave.icebreaker.core.navigation.NavigationRouteRepository;
import dev.overwave.icebreaker.core.serialization.SerializationUtils;
import dev.overwave.icebreaker.core.ship.IceClass;
import dev.overwave.icebreaker.core.ship.Ship;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultRouteService {
    private final static float REFERENCE_SPEED = 20.0F;

    private final NavigationRouteRepository navigationRouteRepository;
    private final DefaultRouteRepository defaultRouteRepository;
    private final VelocityIntervalRepository velocityIntervalRepository;
    private final ObjectMapper objectMapper;

    public void createAllDefaultRoutes() {
        List<NavigationRoute> edges = navigationRouteRepository.findAll();
        List<VelocityInterval> intervals = velocityIntervalRepository.findAll();
        Graph graph = SerializationUtils.readWeightedGraph("data/graph.lz4");

        for (NavigationRoute edge : edges) {
            for (VelocityInterval interval : intervals) {
                for (IceClass iceClass : IceClass.values()) {
                    Ship ship = new Ship("ship", iceClass, REFERENCE_SPEED,
                            iceClass.getGroup().isIcebreaker(), null, null);
                    Point from = edge.getPoint1().getPoint();
                    Point to = edge.getPoint2().getPoint();
                    Optional<Route> routeO = Router.createRoute(from, to, interval.getStartDate(), graph, ship);
                    DefaultRoute defaultRoute = DefaultRoute.builder()
                            .edge(edge)
                            .iceClass(iceClass)
                            .velocityInterval(interval)
                            .travelTime(routeO.map(r -> r.interval().duration()).orElse(Duration.ZERO))
                            .distance(routeO.map(Route::distance).orElse(Float.POSITIVE_INFINITY))
                            .possible(routeO.isPresent())
                            .nodes(serializeNodes(routeO))
                            .build();
                    defaultRouteRepository.save(defaultRoute);
                }
            }
        }

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
}
