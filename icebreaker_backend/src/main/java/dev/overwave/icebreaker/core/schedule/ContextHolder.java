package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalMapper;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalRepository;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalStatic;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.navigation.NavigationPointMapper;
import dev.overwave.icebreaker.core.navigation.NavigationPointRepository;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRequestMapper;
import dev.overwave.icebreaker.core.navigation.NavigationRequestRepository;
import dev.overwave.icebreaker.core.navigation.NavigationRequestStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRouteMapper;
import dev.overwave.icebreaker.core.navigation.NavigationRouteRepository;
import dev.overwave.icebreaker.core.navigation.NavigationRouteStatic;
import dev.overwave.icebreaker.core.navigation.RequestStatus;
import dev.overwave.icebreaker.core.route.DefaultRoute;
import dev.overwave.icebreaker.core.route.DefaultRouteMapper;
import dev.overwave.icebreaker.core.route.DefaultRouteRepository;
import dev.overwave.icebreaker.core.route.DefaultRouteStatic;
import dev.overwave.icebreaker.core.serialization.SerializationUtils;
import dev.overwave.icebreaker.core.ship.ShipMapper;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContextHolder {
    private final NavigationRequestRepository navigationRequestRepository;
    private final NavigationRequestMapper navigationRequestMapper;

    private final NavigationPointRepository navigationPointRepository;
    private final NavigationPointMapper navigationPointMapper;

    private final NavigationRouteRepository navigationRouteRepository;
    private final NavigationRouteMapper navigationRouteMapper;

    private final DefaultRouteRepository defaultRouteRepository;
    private final DefaultRouteMapper defaultRouteMapper;

    private final ShipRepository shipRepository;
    private final ShipMapper shipMapper;

    private final VelocityIntervalRepository velocityIntervalRepository;
    private final VelocityIntervalMapper velocityIntervalMapper;

    private Graph graph;
    private MetaRouteContext context;

    public void readGraph() {
        try {
            log.info("Graph reading...");
            graph = SerializationUtils.readWeightedGraph("data/graph.lz4");
            log.info("Graph read successfully");
        } catch (Exception e) {
            log.error("Failed to read graph!", e);
        }
    }

    public Graph graph() {
        return graph;
    }

    @Transactional
    public synchronized void readContext() {
        log.info("Context reading...");
        Map<Long, NavigationRequestStatic> requests =
                navigationRequestRepository.findAllByStatus(RequestStatus.PENDING).stream()
                        .map(navigationRequestMapper::toNavigationRequestStatic)
                        .collect(Collectors.toMap(NavigationRequestStatic::id, nr -> nr));
        Map<Long, NavigationPointStatic> points = navigationPointRepository.findAll().stream()
                .map(navigationPointMapper::toNavigationPointStatic)
                .collect(Collectors.toMap(NavigationPointStatic::id, np -> np));
        Map<Long, NavigationRouteStatic> routes =
                navigationRouteRepository.findAll().stream()
                        .map(navigationRouteMapper::toNavigationRouteStatic)
                        .collect(Collectors.toMap(NavigationRouteStatic::id, nr -> nr));
        Map<Long, List<DefaultRouteStatic>> defaultRouteByRouteId = defaultRouteRepository.findAll().stream()
                .filter(DefaultRoute::isPossible)
                .map(defaultRouteMapper::toDefaultRouteStatic)
                .collect(Collectors.groupingBy(DefaultRouteStatic::routeId));
        Map<Long, ShipStatic> ships = shipRepository.findAll().stream()
                .map(shipMapper::toShipStatic)
                .collect(Collectors.toMap(ShipStatic::id, s -> s));
        Map<Long, VelocityIntervalStatic> velocities = velocityIntervalRepository.findAll().stream()
                .sorted(Comparator.comparing(VelocityInterval::getStartDate))
                .map(velocityIntervalMapper::toVelocityIntervalStatic)
                .collect(Collectors.toMap(VelocityIntervalStatic::id, s -> s, (s1, s2) -> s1, LinkedHashMap::new));

        context = new MetaRouteContext(ships, requests, points, routes, defaultRouteByRouteId, velocities);
        log.info("Context read successfully");
    }

    public MetaRouteContext context() {
        return context;
    }
}
