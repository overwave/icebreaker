package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.VelocityInterval;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalMapper;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalRepository;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalStatic;
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
import dev.overwave.icebreaker.core.ship.ShipMapper;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SequencedCollection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
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

        predictFullRoutes(ships, convoyRequests, context);
        assignIcebreakers(ships, convoyRequests, context);

        System.out.println(convoyRequests);
    }

    private void assignIcebreakers(List<ScheduledShip> ships, List<ConvoyRequest> convoyRequests,
                                   MetaRouteContext context) {
        List<ConvoyRequest> closestConvoyRequests = convoyRequests.stream()
                .collect(Collectors.toMap(cr -> cr.getShip().getShipId(), cr -> cr, this::getEarlierRequest))
                .values().stream()
                .sorted(Comparator.comparing(cr -> cr.getRouteSegment().interval().start()))
                .toList();
        List<ScheduledShip> icebreakers = ships.stream().filter(ScheduledShip::isIcebreaker).toList();

        for (ConvoyRequest convoyRequest : closestConvoyRequests) {
            NavigationPointStatic target = convoyRequest.getRouteSegment().from();

            for (ScheduledShip icebreaker : icebreakers) {
                ScheduledShip virtualIcebreaker = ScheduledShip.builder()
                        .shipId(icebreaker.getShipId())
                        .currentNavigationPointId(icebreaker.getNextNavigationPointId())
                        .status(ScheduleStatus.FREE)
                        .icebreaker(true)
                        .finishNavigationPointId(icebreaker.getFinishNavigationPointId())
                        .actionEndEta(icebreaker.getActionEndEta())
                        .build();
                Duration etaToFinish = estimateEtaToFinish(context, virtualIcebreaker);
                virtualIcebreaker
                        .setCurrentNavigationPointId(icebreaker.getFinishNavigationPointId())
                        .setFinishNavigationPointId(target.id())
                        .setActionEndEta(icebreaker.getActionEndEta().plus(etaToFinish));
                Duration etaToFinishSecond = estimateEtaToFinish(context, virtualIcebreaker);
                log.info("IBR {} ETA is {}", context.ships().get(icebreaker.getShipId()).name(),
                        asLDT(icebreaker.getActionEndEta().plus(etaToFinish).plus(etaToFinishSecond)));
            }
        }
    }

    private Duration estimateEtaToFinish(MetaRouteContext context, ScheduledShip virtualIcebreaker) {
        List<RoutePredictionSegment> route = predictRoute(virtualIcebreaker, context);
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
            for (RoutePredictionSegment segment : prediction) {
                log.info("From {} to {}, [{} -> {}]", segment.from().name(), segment.to().name(),
                        asLDT(segment.interval().start()), asLDT(segment.interval().end()));
            }
            if (prediction.isEmpty()) {
                freeShip.setStatus(ScheduleStatus.STUCK);
                freeShip.setActionEndEta(Instant.MAX);
                continue;
            }
            RoutePredictionSegment firstPoint = prediction.getFirst();
            freeShip.setStatus(firstPoint.convoy() ? ScheduleStatus.WAITING_CONVOY : ScheduleStatus.WAITING);
            freeShip.setNextNavigationPointId(firstPoint.to().id());
            freeShip.setActionEndEta(firstPoint.interval().instant());

            for (RoutePredictionSegment route : prediction) {
                if (route.convoy()) {
                    convoyRequests.add(new ConvoyRequest(freeShip, null, route));
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
        List<RoutePredictionSegment> route = List.of();
        while (route.isEmpty()) {
            route = metaRouter.createRoute(from, to, ship, startAt, context);
            if (!route.isEmpty()) {
                log.info("Route found!");
                break;
            }
            Interval interval = getInterval(startAt, context.velocities().values());
            Instant afterWaiting = interval.end();
            if (afterWaiting.equals(startAt)) {
                log.info("Exceeded time range!");
                return List.of();
            }
            log.info("Skipping to next interval...");
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
