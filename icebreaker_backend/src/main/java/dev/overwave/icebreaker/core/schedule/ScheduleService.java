package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalMapper;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalRepository;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalStatic;
import dev.overwave.icebreaker.core.graph.Graph;
import dev.overwave.icebreaker.core.navigation.MovementType;
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
import dev.overwave.icebreaker.core.route.Route;
import dev.overwave.icebreaker.core.route.Router;
import dev.overwave.icebreaker.core.serialization.SerializationUtils;
import dev.overwave.icebreaker.core.ship.ShipMapper;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    public static final Duration DURATION_MAX = Duration.ofDays(10000);
    private static Graph GRAPH;

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

    private final IcebreakerLocationRepository icebreakerLocationRepository;
    private final MetaRouter metaRouter;

    @PostConstruct
    void tryReadGraph() {
        try {
            GRAPH = SerializationUtils.readWeightedGraph("data/graph.lz4");
        } catch (Exception e) {
            log.error("Failed to read graph!", e);
        }
    }

    public void createSchedule() {
        Instant now = Instant.EPOCH;
        List<ScheduledShip> ships = new ArrayList<>(); // priority queue by action end eta?
        List<ConvoyRequest> convoyRequests = new ArrayList<>();
        MetaRouteContext context = createContext();

        icebreakerLocationRepository.findAll().stream()
                .map(il -> ScheduledShip.builder()
                        .shipId(il.getIcebreaker().getId())
                        .currentNavigationPointId(il.getStartPoint().getId())
                        .status(ScheduleStatus.FREE)
                        .icebreaker(true)
                        .nextNavigationPointId(il.getStartPoint().getId())
                        .finishNavigationPointId(null)
                        .actionEndEta(il.getStartDate())
                        .convoyRequests(new ArrayList<>())
                        .build())
                .forEach(ships::add);

        context.requests().values().stream()
                .map(nr -> ScheduledShip.builder()
                        .shipId(nr.shipId())
                        .currentNavigationPointId(nr.startPointId())
                        .status(ScheduleStatus.FREE)
                        .icebreaker(false)
                        .nextNavigationPointId(nr.startPointId())
                        .finishNavigationPointId(nr.finishPointId())
                        .actionEndEta(nr.startDate())
                        .convoyRequests(new ArrayList<>())
                        .build())
                .forEach(ships::add);


        while (areMovingShips(ships)) {
            predictFullRoutes(ships, convoyRequests, context);
            assignIcebreakers(ships, convoyRequests, context);

            now = updateTime(now, ships, context);
        }
        System.out.println(convoyRequests);
    }

    private boolean areMovingShips(List<ScheduledShip> ships) {
        for (ScheduledShip ship : ships) {
            if (ship.isIcebreaker() && ship.getStatus() != ScheduleStatus.FREE) {
                return true;
            }
            if (!ship.isIcebreaker() && !Set.of(ScheduleStatus.STUCK, ScheduleStatus.ARRIVED).contains(ship.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private Instant updateTime(Instant now, List<ScheduledShip> ships, MetaRouteContext context) {
        ships.sort(Comparator.comparing(ScheduledShip::getActionEndEta));
        Instant nextTick = ships.getFirst().getActionEndEta();
        for (ScheduledShip ship : ships) {
            if (nextTick.isBefore(ship.getActionEndEta())) {
                break;
            }
            processTick(ship, now, context);
        }
        return nextTick;
    }

    private void processTick(ScheduledShip ship, Instant now, MetaRouteContext context) {
        switch (ship.getStatus()) {
            case FREE -> {
                NavigationPointStatic from = context.points().get(ship.getCurrentNavigationPointId());
                NavigationPointStatic to = context.points().get(ship.getNextNavigationPointId());
                Map<Point, Node> nodes = Router.findClosestNodes(GRAPH, from.point(), to.point());
                Duration referenceDuration = Duration.between(now, ship.getActionEndEta()).multipliedBy(2);
                Optional<Route> route = Router.createRoute(nodes.get(from.point()), nodes.get(to.point()), now,
                        context.ships().get(ship.getShipId()), MovementType.INDEPENDENT, referenceDuration);

                if (route.isPresent()) {
                    ship.setStatus(ScheduleStatus.MOVING)
                            .setActionEndEta(route.get().interval().end());
                } else {
                    // не смогли построить маршрут???
                    ship.setStatus(ScheduleStatus.WAITING)
                            .setActionEndEta(now.plus(1, ChronoUnit.DAYS));
                }
            }
            default -> {
                System.out.println(ship);
            }
        }
    }

    private void assignIcebreakers(List<ScheduledShip> ships, List<ConvoyRequest> convoyRequests,
                                   MetaRouteContext context) {
        List<ConvoyRequest> closestConvoyRequests = convoyRequests.stream()
                .collect(Collectors.toMap(cr -> cr.getShip().getShipId(), cr -> cr, this::getEarlierRequest))
                .values().stream()
                .sorted(Comparator.comparing(cr -> cr.getRouteSegment().interval().start()))
                .toList();
        List<ScheduledShip> icebreakers =
                ships.stream().filter(ScheduledShip::isIcebreaker).collect(Collectors.toList());

        for (ConvoyRequest convoyRequest : closestConvoyRequests) {
            if (icebreakers.isEmpty()) {
                break;
            }
            NavigationPointStatic target = convoyRequest.getRouteSegment().from();

            Instant fastestArrival = Instant.MAX;
            ScheduledShip fastestIcebreaker = null;
            for (ScheduledShip icebreaker : icebreakers) {
                long finishPointId = icebreaker.getFinishNavigationPointId() == null ?
                        icebreaker.getCurrentNavigationPointId() : icebreaker.getFinishNavigationPointId();
                ScheduledShip virtualIcebreaker = ScheduledShip.builder()
                        .shipId(icebreaker.getShipId())
                        .currentNavigationPointId(icebreaker.getNextNavigationPointId())
                        .status(ScheduleStatus.FREE)
                        .icebreaker(true)
                        .finishNavigationPointId(finishPointId)
                        .actionEndEta(icebreaker.getActionEndEta())
                        .build();
                Duration etaToFinish = estimateEtaToFinish(context, virtualIcebreaker);
                if (etaToFinish == null) {
                    continue;
                }
                virtualIcebreaker
                        .setCurrentNavigationPointId(finishPointId)
                        .setFinishNavigationPointId(target.id())
                        .setActionEndEta(icebreaker.getActionEndEta().plus(etaToFinish));
                Duration etaToFinishSecond = estimateEtaToFinish(context, virtualIcebreaker);
                if (etaToFinishSecond == null) {
                    continue;
                }
                Instant etaInstant = icebreaker.getActionEndEta().plus(etaToFinish).plus(etaToFinishSecond);
                if (fastestArrival.isAfter(etaInstant)) {
                    fastestArrival = etaInstant;
                    fastestIcebreaker = icebreaker;
                }
            }
            if (fastestIcebreaker == null) {
                log.info("No icebreakers available, skipped");
                continue;
            }
            icebreakers.remove(fastestIcebreaker);
            if (fastestIcebreaker.getStatus() != ScheduleStatus.FREE) {
                log.info("Fastest icebreaker is busy, not assigned");
                continue;
            }
            fastestIcebreaker.setFinishNavigationPointId(convoyRequest.getRouteSegment().to().id());
            List<RoutePredictionSegment> route = predictRoute(fastestIcebreaker, context);
            convoyRequest.setIcebreaker(fastestIcebreaker);
            fastestIcebreaker.getConvoyRequests().add(convoyRequest);
            log.info("Icebreaker {} assigned to request {}", context.ships().get(fastestIcebreaker.getShipId()),
                    convoyRequest);
        }
    }

    private Duration estimateEtaToFinish(MetaRouteContext context, ScheduledShip virtualIcebreaker) {
        List<RoutePredictionSegment> route = predictRoute(virtualIcebreaker, context);
        if (route == null) {
            return null;
        }
        return route.stream().map(r -> r.interval().duration()).reduce(Duration::plus).orElse(Duration.ZERO);
    }

    private ConvoyRequest getEarlierRequest(ConvoyRequest cr1, ConvoyRequest cr2) {
        return cr1.getRouteSegment().interval().start().compareTo(cr2.getRouteSegment().interval().start()) < 0 ?
                cr1 : cr2;
    }

    private MetaRouteContext createContext() {
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

        return new MetaRouteContext(ships, requests, points, routes, defaultRouteByRouteId, velocities);
    }

    private void predictFullRoutes(List<ScheduledShip> ships, List<ConvoyRequest> convoyRequests,
                                   MetaRouteContext context) {
        List<ScheduledShip> freeShips = ships.stream()
                .filter(s -> s.getStatus() == ScheduleStatus.FREE && !s.isIcebreaker())
                .toList();
        for (ScheduledShip freeShip : freeShips) {
            log.info("_____________________________________________________");
            log.info("Predicting route for " + context.ships().get(freeShip.getShipId()).name());
            List<RoutePredictionSegment> prediction = predictRoute(freeShip, context);
            if (prediction == null) {
                log.info("Ship {} is stuck", context.ships().get(freeShip.getShipId()).name());
                freeShip.setStatus(ScheduleStatus.STUCK);
                freeShip.setActionEndEta(Instant.MAX);
                continue;
            }
            for (RoutePredictionSegment segment : prediction) {
                log.info("From {} to {}, [{} -> {}]", segment.from().name(), segment.to().name(),
                        asLDT(segment.interval().start()), asLDT(segment.interval().end()));
            }
            RoutePredictionSegment firstPoint = prediction.getFirst();
            freeShip.setStatus(firstPoint.convoy() ? ScheduleStatus.WAITING_CONVOY : ScheduleStatus.WAITING);
            freeShip.setNextNavigationPointId(firstPoint.to().id());
            freeShip.setActionEndEta(firstPoint.interval().instant());

            for (RoutePredictionSegment route : prediction) {
                if (route.convoy()) {
                    ConvoyRequest convoyRequest = new ConvoyRequest(freeShip, null, route);
                    freeShip.getConvoyRequests().add(convoyRequest);
                    convoyRequests.add(convoyRequest);
                }
            }
        }
    }

    private static LocalDateTime asLDT(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
    }

    private List<RoutePredictionSegment> predictRoute(ScheduledShip scheduledShip, MetaRouteContext context) {
        if (scheduledShip.getStatus() != ScheduleStatus.FREE) {
            throw new IllegalStateException();
        }
        ShipStatic ship = context.ships().get(scheduledShip.getShipId());
        NavigationPointStatic from = context.points().get(scheduledShip.getCurrentNavigationPointId());
        NavigationPointStatic to = context.points().get(scheduledShip.getFinishNavigationPointId());
        Instant startAt = scheduledShip.getActionEndEta();
        List<RoutePredictionSegment> route;
        while (true) {
            route = metaRouter.createRoute(from, to, ship, startAt, context);
            if (route != null && route.isEmpty()) {
                // already there
                return List.of();
            }
            if (route != null) {
//                log.info("Route found!");
                break;
            }
            Interval interval = getInterval(startAt, context.velocities().values());
            Instant afterWaiting = interval.end();
            if (afterWaiting.equals(startAt)) {
//                log.info("Exceeded time range!");
                return null;
            }
//            log.info("Skipping to next interval...");
            startAt = afterWaiting;

        }
        if (!startAt.equals(scheduledShip.getActionEndEta())) {
            route = Stream.concat(Stream.of(new RoutePredictionSegment(from, from, false,
                            Interval.ofStartEnd(scheduledShip.getActionEndEta(), startAt))),
                    route.stream()).toList();
        }
        return route;
    }

    private Interval getInterval(Instant startAt, Collection<VelocityIntervalStatic> intervals) {
        SequencedCollection<VelocityIntervalStatic> intervalSequence =
                (SequencedCollection<VelocityIntervalStatic>) intervals;
        // Раньше первого интервала
        if (startAt.compareTo(intervalSequence.getFirst().interval().start()) <= 0) {
            return Interval.ofStartEnd(startAt, intervalSequence.getFirst().interval().start());
        }
        return intervalSequence.stream()
                .filter(vi -> vi.interval().contains(startAt))
                .findFirst()
                .map(VelocityIntervalStatic::interval)
                // Позже последнего интервала
                .orElse(intervalSequence.getLast().interval());
    }
}

