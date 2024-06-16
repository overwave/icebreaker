package dev.overwave.icebreaker.core.schedule;

import dev.overwave.icebreaker.api.navigation.ShipRouteDto;
import dev.overwave.icebreaker.core.geospatial.Interval;
import dev.overwave.icebreaker.core.geospatial.Node;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.geospatial.VelocityIntervalStatic;
import dev.overwave.icebreaker.core.navigation.MovementType;
import dev.overwave.icebreaker.core.navigation.NavigationPointStatic;
import dev.overwave.icebreaker.core.navigation.NavigationRequest;
import dev.overwave.icebreaker.core.navigation.NavigationRequestMapper;
import dev.overwave.icebreaker.core.navigation.NavigationRequestRepository;
import dev.overwave.icebreaker.core.navigation.NavigationRequestStatic;
import dev.overwave.icebreaker.core.navigation.RequestStatus;
import dev.overwave.icebreaker.core.parser.ScheduleSegment;
import dev.overwave.icebreaker.core.parser.ShipSchedule;
import dev.overwave.icebreaker.core.parser.XlsxParser;
import dev.overwave.icebreaker.core.route.Route;
import dev.overwave.icebreaker.core.route.Router;
import dev.overwave.icebreaker.core.schedule.ConfirmedRouteSegment.ConfirmedRouteSegmentBuilder;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.ship.ShipStatic;
import dev.overwave.icebreaker.core.user.User;
import dev.overwave.icebreaker.core.user.UserRepository;
import dev.overwave.icebreaker.core.user.UserRole;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedCollection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ContextHolder contextHolder;
    private final ShipRouteMapper shipRouteMapper;

    private final IcebreakerLocationRepository icebreakerLocationRepository;
    private final NavigationRequestRepository navigationRequestRepository;
    private final NavigationRequestMapper navigationRequestMapper;
    private final ShipRouteRepository shipRouteRepository;
    private final ShipRepository shipRepository;
    private final UserRepository userRepository;
    private final ShipRouteService shipRouteService;
    private final MetaRouter metaRouter;

    @PostConstruct
    void tryReadGraph() {
        new Thread(contextHolder::readGraph).start();
        new Thread(contextHolder::readContext).start();
    }

    public byte[] createFileWithScheduleForIcebreaker(long icebreakerId) {
        Ship icebreaker = shipRepository.findByIdOrThrow(icebreakerId);
        List<ShipSchedule> shipSchedules = new ArrayList<>();
        List<ScheduleSegment> segments =
                    shipRouteRepository.findAllByShipIdOrderById(icebreakerId)
                            .stream()
                            .map(entity -> new ScheduleSegment(
                                    entity.getStartPoint().getName(),
                                    entity.getFinishPoint().getName(),
                                    LocalDate.ofInstant(entity.getStartDate(), ZoneOffset.UTC),
                                    LocalDate.ofInstant(entity.getEndDate(), ZoneOffset.UTC)))
                            .toList();

            shipSchedules.add(new ShipSchedule(icebreaker.getName(), segments));

        LocalDate firstDate = segments.getFirst().startDate();
        LocalDate finishDate = segments.getLast().finishDate();

        return XlsxParser.createFileWithGanttDiagram(shipSchedules, firstDate, finishDate);
    }

    public byte[] createFileWithSchedule(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        List<NavigationRequest> requests = new ArrayList<>();
        if(user.getRole() == UserRole.CAPTAIN) {
            requests = user.getShips().stream()
                    .flatMap(ship -> ship.getNavigationRequests().stream())
                    .filter(request -> request.getStatus() == RequestStatus.APPROVED)
                    .toList();
        } else {
            requests = navigationRequestRepository.findAllByStatus(RequestStatus.APPROVED);
        }
        List<ShipSchedule> shipSchedules = new ArrayList<>();
        LocalDate firstDate = LocalDate.MAX;
        LocalDate finishDate= LocalDate.MIN;
        for (NavigationRequest request : requests) {
            List<ScheduleSegment> segments =
                    shipRouteRepository.findAllByNavigationRequestIdOrderById(request.getId())
                            .stream()
                            .map(entity -> new ScheduleSegment(
                                    entity.getStartPoint().getName(),
                                    entity.getFinishPoint().getName(),
                                    LocalDate.ofInstant(entity.getStartDate(), ZoneOffset.UTC),
                                    LocalDate.ofInstant(entity.getEndDate(), ZoneOffset.UTC)))
                                    .collect(Collectors.toList());
            if (!segments.isEmpty()
                    && segments.getFirst().startPointName().equals(segments.getFirst().finishPointName())) {
                segments.removeFirst();
            }

            shipSchedules.add(new ShipSchedule(request.getShip().getName(), segments));
            LocalDate currentFirstDate = segments.getFirst().startDate();
            if(currentFirstDate.isBefore(firstDate)) {
                firstDate = currentFirstDate;
            }
            LocalDate currentFinishDate = segments.getLast().finishDate();
            if(currentFinishDate.isAfter(finishDate)) {
                finishDate = currentFinishDate;
            }
        }
        shipSchedules.sort(Comparator.comparing(s -> s.segments().stream()
                .findFirst()
                .map(ScheduleSegment::startDate)
                .orElse(LocalDate.MIN)));
        return XlsxParser.createFileWithGanttDiagram(shipSchedules, firstDate, finishDate);
    }

    public List<ShipRouteDto> createPreliminaryShipRoute(long shipId) {
        List<ScheduledShip> ships = new ArrayList<>();
        MetaRouteContext context = contextHolder.context();

        icebreakerLocationRepository.findAll().stream()
                .map(il -> ScheduledShip.builder()
                        .shipId(il.getIcebreaker().getId())
                        .currentNavigationPointId(il.getStartPoint().getId())
                        .status(ScheduleStatus.WAITING)
                        .icebreaker(true)
                        .nextNavigationPointId(il.getStartPoint().getId())
                        .finishNavigationPointId(null)
                        .actionEndEta(il.getStartDate())
                        .build())
                .forEach(ships::add);

        Map<Long, NavigationRequestStatic> requests =
                navigationRequestRepository.findAll().stream()
                        .filter(nr -> nr.getStatus() != RequestStatus.REJECTED)
                        .map(navigationRequestMapper::toNavigationRequestStatic)
                        .collect(Collectors.toMap(NavigationRequestStatic::id, nr -> nr));
        requests.values().stream()
                .map(nr -> ScheduledShip.builder()
                        .shipId(nr.shipId())
                        .currentNavigationPointId(nr.startPointId())
                        .status(ScheduleStatus.WAITING)
                        .icebreaker(false)
                        .nextNavigationPointId(nr.startPointId())
                        .finishNavigationPointId(nr.finishPointId())
                        .actionEndEta(nr.startDate())
                        .build())
                .forEach(ships::add);

        ScheduledShip ship = ships.stream()
                .filter(scheduledShip -> scheduledShip.getShipId() == shipId)
                .findFirst()
                .orElseThrow();
        List<RoutePredictionSegment> prediction = predictRoute(ship);
        List<ShipRouteDto> routes = new ArrayList<>();
        for (int i = 0; i < prediction.size(); i++) {
            RoutePredictionSegment segment = prediction.get(i);
            NavigationPointStatic from = segment.from();
            NavigationPointStatic to = segment.to();
            MovementType movementType = segment.convoy() ? MovementType.FOLLOWING : MovementType.INDEPENDENT;
            Map<Point, Node> nodes = Router.findClosestNodes(contextHolder.graph(), from.point(), to.point());
            Route route = Router.createRoute(nodes.get(from.point()), nodes.get(to.point()), segment.interval().start(),
                    context.ships().get(shipId), movementType, Duration.ZERO).orElseThrow();

            ScheduledShip icebreaker = ships.stream().filter(ScheduledShip::isIcebreaker).findFirst().orElseThrow();
            routes.add(shipRouteMapper.toShipRouteDto(i, segment, route));
        }
        return routes;
    }

    public void createSchedule() {
        shipRouteRepository.deleteAll();

        Instant now = Instant.EPOCH;
        List<ScheduledShip> ships = new ArrayList<>(); // priority queue by action end eta?
        List<ConvoyRequest> convoyRequests = new ArrayList<>();

        icebreakerLocationRepository.findAll().stream()
//                .limit(2)
                .map(il -> ScheduledShip.builder()
                        .shipId(il.getIcebreaker().getId())
                        .currentNavigationPointId(il.getStartPoint().getId())
                        .status(ScheduleStatus.WAITING)
                        .icebreaker(true)
                        .nextNavigationPointId(il.getStartPoint().getId())
                        .finishNavigationPointId(null)
                        .actionEndEta(il.getStartDate())
                        .build())
                .forEach(ships::add);

        Map<Long, NavigationRequestStatic> requests =
                navigationRequestRepository.findAll().stream()
                        .filter(nr -> nr.getStatus() != RequestStatus.REJECTED)
                        .map(navigationRequestMapper::toNavigationRequestStatic)
                        .collect(Collectors.toMap(NavigationRequestStatic::id, nr -> nr));
        requests.values().stream()
                .filter(r -> r.startDate().isBefore(Instant.parse("2024-05-01T12:00:00Z")))
//                .limit(6)
                .map(nr -> ScheduledShip.builder()
                        .shipId(nr.shipId())
                        .requestId(nr.id())
                        .currentNavigationPointId(nr.startPointId())
                        .status(ScheduleStatus.WAITING)
                        .icebreaker(false)
                        .nextNavigationPointId(nr.startPointId())
                        .finishNavigationPointId(nr.finishPointId())
                        .actionEndEta(nr.startDate())
                        .build())
                .forEach(ships::add);


        while (areMovingShips(ships)) {
            predictFullRoutes(ships, convoyRequests);
            assignIcebreakers(ships, convoyRequests);

            now = updateTime(now, ships);
            convoyRequests.clear();
            ships.forEach(s -> s.getConvoyRequests().clear());
        }
        shipRouteService.saveRoutes(ships);
    }

    private boolean areMovingShips(List<ScheduledShip> ships) {
        for (ScheduledShip ship : ships) {
            if (ship.isIcebreaker()) {
                if (ship.getStatus() != ScheduleStatus.FREE) {
                    return true;
                }
            } else {
                Set<ScheduleStatus> finishStatuses = Set.of(ScheduleStatus.STUCK, ScheduleStatus.ARRIVED);
                if (!finishStatuses.contains(ship.getStatus())) {
                    return true;
                }
            }
        }
        return false;
    }

    private Instant updateTime(Instant now, List<ScheduledShip> ships) {
        Instant nextTick = Instant.MAX;
        ships.sort(Comparator.comparing(ScheduledShip::isIcebreaker).reversed()); // icebreakers first
        for (ScheduledShip ship : ships) {
            Set<ScheduleStatus> readyStatuses = Set.of(ScheduleStatus.READY_TO_MOVE, ScheduleStatus.READY_TO_CONVOY);
            if (readyStatuses.contains(ship.getStatus())) {
                processTickBefore(ship, now);
            }
            nextTick = (ship.getActionEndEta().isAfter(now) && ship.getActionEndEta().isBefore(nextTick)) ?
                    ship.getActionEndEta() : nextTick;
        }
        if (nextTick.equals(Instant.MAX)) {
            nextTick = now.plus(Duration.ofMinutes(1));
        }
        for (ScheduledShip ship : ships) {
//            if (nextTick.isAfter(ship.getActionEndEta())) {
// плохо!
//                System.out.println(ship);
//            }
            if (!nextTick.isBefore(ship.getActionEndEta())) {
                processTickAfter(ship, nextTick);
            }
        }
        if (nextTick.equals(Instant.parse("2024-04-28T00:00:00Z"))) {
            System.out.println(nextTick);
        }
        return nextTick;
    }

    private void processTickBefore(ScheduledShip ship, Instant now) {
        MetaRouteContext context = contextHolder.context();
        switch (ship.getStatus()) {
            case READY_TO_MOVE -> {
                NavigationPointStatic from = context.points().get(ship.getCurrentNavigationPointId());
                NavigationPointStatic to = context.points().get(ship.getNextNavigationPointId());
                Map<Point, Node> nodes = Router.findClosestNodes(contextHolder.graph(), from.point(), to.point());
                Duration referenceDuration = Duration.between(now, ship.getActionEndEta()).multipliedBy(5);
                Optional<Route> routeO = Router.createRoute(nodes.get(from.point()), nodes.get(to.point()), now,
                        context.ships().get(ship.getShipId()), MovementType.INDEPENDENT, referenceDuration);

                if (routeO.isPresent()) {
                    Route route = routeO.get();
                    ConfirmedRouteSegment confirmedRouteSegment = ConfirmedRouteSegment.builder()
                            .from(from)
                            .to(to)
                            .interval(route.interval())
                            .points(route.normalizedPoints())
                            .convoy(false)
                            .build();
                    ship.setStatus(ScheduleStatus.MOVING)
                            .setActionEndEta(route.interval().end())
                            .getRouteSegments().add(confirmedRouteSegment);
//                    log.error("Processing tick for {}, status {} -> {}", context.ships().get(ship.getShipId()),
//                            ScheduleStatus.READY_TO_MOVE, ScheduleStatus.MOVING);
                } else {
                    // не смогли построить маршрут???
                    ship.setStatus(ScheduleStatus.WAITING)
                            .setActionEndEta(now.plus(1, ChronoUnit.DAYS));
//                    log.error("Processing tick for {}, status {} -> {}", context.ships().get(ship.getShipId()),
//                            ScheduleStatus.READY_TO_MOVE, ScheduleStatus.WAITING);
                }
            }
            case READY_TO_CONVOY -> processReadyToConvoy(ship, now, context);
            default -> throw new OutOfMemoryError();
        }
    }

    private void processReadyToConvoy(ScheduledShip ship, Instant now, MetaRouteContext context) {
        ScheduledShip follower = ship.getConvoyRequests().getFirst().getShip();
        if (follower.getStatus() != ScheduleStatus.READY_TO_FOLLOW ||
                follower.getCurrentNavigationPointId() != ship.getCurrentNavigationPointId()) {
            ship.setStatus(ScheduleStatus.FREE)
                    .setActionEndEta(now);
            return;
        }
        // move!
        NavigationPointStatic from = context.points().get(follower.getCurrentNavigationPointId());
        NavigationPointStatic to = context.points().get(follower.getNextNavigationPointId());
        Map<Point, Node> nodes = Router.findClosestNodes(contextHolder.graph(), from.point(), to.point());
        Optional<Route> routeO = Router.createRoute(nodes.get(from.point()), nodes.get(to.point()), now,
                context.ships().get(follower.getShipId()), MovementType.FOLLOWING, Duration.ZERO);

        if (routeO.isEmpty()) {
            // не смогли построить маршрут???
//            log.error("Processing tick for {}, status {} -> {}", context.ships().get(follower.getShipId()),
//                    ScheduleStatus.READY_TO_CONVOY, ScheduleStatus.READY_TO_CONVOY);
            Router.createRoute(nodes.get(from.point()), nodes.get(to.point()), now,
                    context.ships().get(follower.getShipId()), MovementType.FOLLOWING, Duration.ZERO);
            return;
        }
        Route route = routeO.get();
        ConfirmedRouteSegmentBuilder builder = ConfirmedRouteSegment.builder()
                .from(from)
                .to(to)
                .interval(route.interval())
                .points(route.normalizedPoints())
                .convoy(true);

//        log.error("Processing tick for {}, status {} -> {}", context.ships().get(follower.getShipId()),
//                follower.getStatus(), ScheduleStatus.MOVING);
        ConfirmedRouteSegment followerSegment =
                builder.otherShips(List.of(context.ships().get(ship.getShipId()))).build();
        follower.setStatus(ScheduleStatus.MOVING)
                .setActionEndEta(route.interval().end())
                .getRouteSegments().add(followerSegment);

//        log.error("Processing tick for {}, status {} -> {}", context.ships().get(ship.getShipId()),
//                ScheduleStatus.READY_TO_CONVOY, ScheduleStatus.MOVING);
        ConfirmedRouteSegment icebreakerSegment =
                builder.otherShips(List.of(context.ships().get(follower.getShipId()))).build();
        ship.setStatus(ScheduleStatus.MOVING)
                .setNextNavigationPointId(follower.getNextNavigationPointId())
                .setFinishNavigationPointId(follower.getNextNavigationPointId())
                .setActionEndEta(route.interval().end())
                .getRouteSegments().add(icebreakerSegment);
    }

    private void processTickAfter(ScheduledShip ship, Instant now) {
        ship.setActionEndEta(now);
        switch (ship.getStatus()) {
            case WAITING, READY_TO_FOLLOW, READY_TO_CONVOY -> {
                ship.setStatus(ScheduleStatus.FREE);
                if (ship.isIcebreaker()) {
                    ship.setFinishNavigationPointId(null);
                }
            }
            case MOVING -> ship.setStatus(ScheduleStatus.FREE)
                    .setCurrentNavigationPointId(ship.getNextNavigationPointId());
            case FREE -> {
            }
            default -> throw new OutOfMemoryError();
        }
    }

    private void assignIcebreakers(List<ScheduledShip> ships, List<ConvoyRequest> convoyRequests) {
        List<ConvoyRequest> closestConvoyRequests = convoyRequests.stream()
                .collect(Collectors.toMap(cr -> cr.getShip().getShipId(), cr -> cr, this::getEarlierRequest))
                .values().stream()
                .sorted(Comparator.comparing(cr -> cr.getRouteSegment().interval().start()))
                .toList();
        List<ScheduledShip> icebreakers = ships.stream().filter(ScheduledShip::isIcebreaker).toList();

        for (ConvoyRequest convoyRequest : closestConvoyRequests) {
            if (icebreakers.stream().allMatch(i -> i.getStatus() != ScheduleStatus.FREE)) {
                break;
            }
            NavigationPointStatic target = convoyRequest.getRouteSegment().from();

            Instant fastestArrival = Instant.MAX;
            ScheduledShip fastestIcebreaker = null;
            for (ScheduledShip icebreaker : icebreakers) {
                Instant etaToFinish = getEtaToFinishCurrent(icebreaker, target);
                if (etaToFinish == null) {
                    continue;
                }
                if (fastestArrival.isAfter(etaToFinish)) {
                    fastestArrival = etaToFinish;
                    fastestIcebreaker = icebreaker;
                }
            }
            if (fastestIcebreaker == null) {
                log.info("No icebreakers available, skipped");
                continue;
            }
            if (fastestIcebreaker.getStatus() != ScheduleStatus.FREE) {
                continue;
            }
            fastestIcebreaker.setFinishNavigationPointId(convoyRequest.getRouteSegment().from().id());
            List<RoutePredictionSegment> route = predictRoute(fastestIcebreaker);
            Objects.requireNonNull(route);
            if (route.isEmpty()) {
                fastestIcebreaker.setStatus(ScheduleStatus.READY_TO_CONVOY)
                        .setNextNavigationPointId(fastestIcebreaker.getCurrentNavigationPointId())
                        // Ждём кораблик
                        .setActionEndEta(convoyRequest.getRouteSegment().interval().start());
            } else {
                RoutePredictionSegment routeFirst = route.getFirst();
                fastestIcebreaker.setStatus(ScheduleStatus.READY_TO_MOVE)
                        .setNextNavigationPointId(routeFirst.to().id())
                        .setActionEndEta(routeFirst.interval().end());
            }

            convoyRequest.setIcebreaker(fastestIcebreaker);
            fastestIcebreaker.getConvoyRequests().add(convoyRequest);
//            log.info("Icebreaker {} assigned to request {}", context.ships().get(fastestIcebreaker.getShipId()),
//                    convoyRequest);
        }
    }

    private Instant getEtaToFinishCurrent(ScheduledShip icebreaker, NavigationPointStatic target) {
        long currentPointId = icebreaker.getStatus() == ScheduleStatus.MOVING ? // другие способы езды?
                icebreaker.getNextNavigationPointId() :
                icebreaker.getCurrentNavigationPointId();
        long finishPointId = Stream.of(icebreaker.getFinishNavigationPointId(),
                        icebreaker.getNextNavigationPointId(), icebreaker.getCurrentNavigationPointId())
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
        ScheduledShip virtualIcebreaker = ScheduledShip.builder()
                .shipId(icebreaker.getShipId())
                .currentNavigationPointId(currentPointId)
                .status(ScheduleStatus.FREE)
                .icebreaker(true)
                .finishNavigationPointId(finishPointId)
                .actionEndEta(icebreaker.getActionEndEta())
                .build();
        Duration eta = estimateEtaToFinish(virtualIcebreaker);
        if (eta == null) {
            return null;
        }
        virtualIcebreaker.setCurrentNavigationPointId(finishPointId)
                .setFinishNavigationPointId(target.id())
                .setActionEndEta(icebreaker.getActionEndEta().plus(eta));
        Duration etaAfter = estimateEtaToFinish(virtualIcebreaker);
        if (etaAfter == null) {
            return null;
        }
        return icebreaker.getActionEndEta().plus(eta).plus(etaAfter);
    }

    private Duration estimateEtaToFinish(ScheduledShip virtualIcebreaker) {
        List<RoutePredictionSegment> route = predictRoute(virtualIcebreaker);
        if (route == null) {
            return null;
        }
        return route.stream().map(r -> r.interval().duration()).reduce(Duration::plus).orElse(Duration.ZERO);
    }

    private ConvoyRequest getEarlierRequest(ConvoyRequest cr1, ConvoyRequest cr2) {
        return cr1.getRouteSegment().interval().start().compareTo(cr2.getRouteSegment().interval().start()) < 0 ?
                cr1 : cr2;
    }

    private void predictFullRoutes(List<ScheduledShip> ships, List<ConvoyRequest> convoyRequests) {
        List<ScheduledShip> freeShips = ships.stream()
                .filter(s -> Set.of(ScheduleStatus.FREE, ScheduleStatus.WAITING).contains(s.getStatus()))
                .filter(s -> !s.isIcebreaker())
                .toList();
        for (ScheduledShip freeShip : freeShips) {
//            if (freeShip.getShipId() == 40L) {
//                System.out.println(freeShip);
//            }
            List<RoutePredictionSegment> prediction = predictRoute(freeShip);
            if (prediction == null) {
//                log.info("Ship {} is stuck", context.ships().get(freeShip.getShipId()).name());
                freeShip.setStatus(ScheduleStatus.STUCK);
                freeShip.setActionEndEta(Instant.MAX);
                continue;
            }
            if (prediction.isEmpty()) {
//                log.info("Ship {} has arrived", context.ships().get(freeShip.getShipId()).name());
                freeShip.setStatus(ScheduleStatus.ARRIVED);
                freeShip.setActionEndEta(Instant.MAX);
                continue;
            }
            RoutePredictionSegment firstPoint = prediction.getFirst();
            if (firstPoint.from().equals(firstPoint.to())) {
                // надо подождать
                if (freeShip.getStatus() == ScheduleStatus.FREE) {
                    freeShip.setStatus(ScheduleStatus.WAITING)
                            .setActionEndEta(firstPoint.interval().end());
                }
            } else {
                freeShip.setNextNavigationPointId(firstPoint.to().id());
                if (freeShip.getStatus() == ScheduleStatus.FREE) {
                    freeShip.setStatus(firstPoint.convoy() ? ScheduleStatus.READY_TO_FOLLOW :
                            ScheduleStatus.READY_TO_MOVE);
                }
            }
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

    private List<RoutePredictionSegment> predictRoute(ScheduledShip scheduledShip) {
        MetaRouteContext context = contextHolder.context();
        ShipStatic ship = context.ships().get(scheduledShip.getShipId());
        NavigationPointStatic from = context.points().get(scheduledShip.getCurrentNavigationPointId());
        NavigationPointStatic to = context.points().get(scheduledShip.getFinishNavigationPointId());
        Instant startAt = scheduledShip.getActionEndEta();
        List<RoutePredictionSegment> route;
        while (true) {
            route = metaRouter.createRoute(from, to, ship, startAt, context);
            if (route != null && route.isEmpty()) {
                return List.of();
            }
            if (route != null) {
                break;
            }
            Interval interval = getInterval(startAt, context.velocities().values());
            Instant afterWaiting = interval.end();
            if (afterWaiting.equals(startAt)) {
                return null;
            }
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

