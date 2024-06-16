package dev.overwave.icebreaker.core.navigation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.api.icebreaker.IcebreakerDetailDto;
import dev.overwave.icebreaker.api.icebreaker.IcebreakerDetailListDto;
import dev.overwave.icebreaker.api.icebreaker.IcebreakerRouteDto;
import dev.overwave.icebreaker.api.icebreaker.IcebreakerRouteSegmentDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestPendingDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestToSaveDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestWithRouteDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestsDtoWithRoute;
import dev.overwave.icebreaker.api.navigation.PointAndTimestamp;
import dev.overwave.icebreaker.api.navigation.RouteSegmentDto;
import dev.overwave.icebreaker.api.navigation.ShipRouteDto;
import dev.overwave.icebreaker.api.ship.ShipDto;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.schedule.ContextHolder;
import dev.overwave.icebreaker.core.schedule.ShipRouteEntity;
import dev.overwave.icebreaker.core.schedule.ShipRouteMapper;
import dev.overwave.icebreaker.core.schedule.ShipRouteRepository;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.user.User;
import dev.overwave.icebreaker.core.user.UserRepository;
import dev.overwave.icebreaker.core.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavigationRequestService {
    private final NavigationRequestRepository navigationRequestRepository;
    private final NavigationRequestMapper navigationRequestMapper;
    private final ShipRepository shipRepository;
    private final ShipRouteRepository shipRouteRepository;
    private final NavigationPointRepository navigationPointRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final ShipRouteMapper shipRouteMapper;
    private final ContextHolder contextHolder;

    public void saveNavigationRequest(NavigationRequestToSaveDto requestDto) {
        Ship ship = shipRepository.findByIdOrThrow(requestDto.shipId());
        NavigationPoint startPoint = navigationPointRepository.findByIdOrThrow(requestDto.startPointId());
        NavigationPoint finishPoint = navigationPointRepository.findByIdOrThrow(requestDto.finishPointId());
        NavigationRequest navigationRequest = navigationRequestMapper.toNavigationRequest(requestDto, ship,
                startPoint, finishPoint, RequestStatus.PENDING);
        navigationRequestRepository.save(navigationRequest);
        new Thread(contextHolder::readContext).start();
    }

    public NavigationRequestsDtoWithRoute getNavigationRequests(String login) {
        List<NavigationRequest> requests = getRequestsByUser(login);
        Map<RequestStatus, List<NavigationRequest>> requestsByStatus = requests.stream()
                .collect(Collectors.groupingBy(NavigationRequest::getStatus));
        return new NavigationRequestsDtoWithRoute(
                mapRequests(requestsByStatus.getOrDefault(RequestStatus.PENDING, List.of())),
                getApprovedRoutes(requestsByStatus.getOrDefault(RequestStatus.APPROVED, List.of())),
                mapRequests(requestsByStatus.getOrDefault(RequestStatus.REJECTED, List.of()))
        );
    }

    private List<NavigationRequestPendingDto> mapRequests(List<NavigationRequest> requests) {
        return requests.stream().map(navigationRequestMapper::toNavigationRequestPendingDto).toList();
    }

    private List<NavigationRequestWithRouteDto> getApprovedRoutes(List<NavigationRequest> requests) {
        return requests.stream().map(request -> {
            Ship ship = request.getShip();
            List<ShipRouteEntity> segments = shipRouteRepository.findAllByNavigationRequestIdOrderById(request.getId());

            boolean convoy = segments.stream().anyMatch(s -> !s.getCompanions().equals("[]"));
            List<RouteSegmentDto> routes = segments.stream()
                    .filter(routeSegment -> !isWaiting(routeSegment))
                    .map(routeSegment -> {
                        List<Long> companions = getCompanions(routeSegment);
                        Optional<Ship> icebreaker =
                                companions.stream().findFirst().map(shipRepository::findByIdOrThrow);
                        String icebreakerName = icebreaker.map(Ship::getName).orElse("");
                        String icebreakerClass = icebreaker.map(s -> s.getIceClass().name()).orElse("");
                        return RouteSegmentDto.builder()
                                .id(routeSegment.getId())
                                .startDate(asLD(routeSegment.getStartDate()))
                                .startPointId(routeSegment.getStartPoint().getId())
                                .startPointName(routeSegment.getStartPoint().getName())
                                .finishDate(asLD(routeSegment.getEndDate()))
                                .finishPointId(routeSegment.getFinishPoint().getId())
                                .finishPointName(routeSegment.getFinishPoint().getName())
                                .icebreakerName(icebreakerName)
                                .icebreakerClass(icebreakerClass)
                                .build();
                    })
                    .toList();
            return NavigationRequestWithRouteDto.builder()
                    .id(request.getId())
                    .shipId(ship.getId())
                    .shipName(ship.getName())
                    .shipClass(ship.getIceClass().name())
                    .convoy(convoy)
                    .routes(routes)
                    .build();
        }).toList();
    }

    private List<NavigationRequest> getRequestsByUser(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        if (user.getRole().equals(UserRole.ADMIN)) {
            return navigationRequestRepository.findAll();
        } else {
            return user.getShips().stream()
                    .flatMap(ship -> ship.getNavigationRequests().stream())
                    .toList();
        }
    }

    public List<NavigationRequestPendingDto> getNavigationRequestsPending(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        if (user.getRole().equals(UserRole.ADMIN)) {
            return mapRequests(navigationRequestRepository.findAllByStatus(RequestStatus.PENDING));
        }
        return user.getShips().stream()
                .flatMap(ship -> ship.getNavigationRequests().stream())
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .map(navigationRequestMapper::toNavigationRequestPendingDto)
                .toList();
    }

    public NavigationRequestDto rejectNavigationRequest(long id) {
        NavigationRequest request = navigationRequestRepository.findByIdOrThrow(id);
        request.setStatus(RequestStatus.REJECTED);
        NavigationRequest saved = navigationRequestRepository.saveAndFlush(request);
        new Thread(contextHolder::readContext).start();
        return navigationRequestMapper.toNavigationRequestDto(saved);
    }

    public List<ShipRouteDto> getShipRouteByRequestId(long navigationRequestId) {
        List<ShipRouteEntity> routeSegments =
                shipRouteRepository.findAllByNavigationRequestIdOrderById(navigationRequestId);
        List<ShipRouteDto> routes = new ArrayList<>();
        Long previousRouteEpoch = null;
        for (ShipRouteEntity routeSegment : routeSegments) {
            if (isWaiting(routeSegment)) {
                continue;
            }
            List<Long> companions = getCompanions(routeSegment);
            ShipRouteDto route = ShipRouteDto.builder().id(routeSegment.getId())
                    .convoy(!companions.isEmpty())
                    .icebreaker(companions.stream().findFirst().map(String::valueOf).orElse(""))
                    .routes(getHourlyPoints(routeSegment, previousRouteEpoch))
                    .build();
            previousRouteEpoch = route.routes().getLast().time();
            routes.add(route);
        }
        return routes;
    }

    private List<PointAndTimestamp> getHourlyPoints(ShipRouteEntity routeSegment, Long previousRouteEpoch) {
        if (isWaiting(routeSegment)) {
            List<PointAndTimestamp> points = new ArrayList<>();
            for (Instant i = routeSegment.getStartDate().truncatedTo(ChronoUnit.HOURS);
                 i.isBefore(routeSegment.getEndDate());
                 i = i.plus(Duration.ofHours(1))) {
                points.add(new PointAndTimestamp(routeSegment.getStartPoint().getPoint(), i.getEpochSecond()));
            }
            return points;
        } else {
            List<Point> points = getPoints(routeSegment);
            List<PointAndTimestamp> timestamps =
                    shipRouteMapper.createPointAndTimestamp(routeSegment.getStartDate(), points);
            if (previousRouteEpoch != null) {
                timestamps = timestamps.stream().dropWhile(t -> t.time() <= previousRouteEpoch).toList();
            }
            return timestamps;
        }
    }

    private boolean isWaiting(ShipRouteEntity routeSegment) {
        return routeSegment.getStartPoint().getPoint().equals(routeSegment.getFinishPoint().getPoint());
    }

    @SneakyThrows
    private List<Long> getCompanions(ShipRouteEntity routeSegment) {
        TypeReference<List<Long>> listLong = new TypeReference<>() {
        };
        return objectMapper.readValue(routeSegment.getCompanions(), listLong);
    }

    @SneakyThrows
    private List<Point> getPoints(ShipRouteEntity routeSegment) {
        TypeReference<List<Point>> listPoints = new TypeReference<>() {
        };
        return objectMapper.readValue(routeSegment.getPoints(), listPoints);
    }

    public IcebreakerDetailListDto getIcebreakersDetails() {
        List<Ship> icebreakers = shipRepository.findAllByIcebreaker(true);
        List<IcebreakerDetailDto> icebreakerDetails = icebreakers.stream()
                .map(icebreaker -> IcebreakerDetailDto.builder()
                        .id(icebreaker.getId())
                        .name(icebreaker.getName())
                        .speed(icebreaker.getSpeed())
                        .iceClass(icebreaker.getIceClass().name())
                        .route(getRoute(icebreaker))
                        .build())
                .toList();
        return new IcebreakerDetailListDto(icebreakerDetails);
    }

    private List<IcebreakerRouteSegment> getRoute(Ship icebreaker) {
        return shipRouteRepository.findAllByShipIdOrderById(icebreaker.getId()).stream()
                .map(routeSegment -> {
                    List<ShipDto> companions = getCompanions(routeSegment).stream()
                            .map(shipRepository::findByIdOrThrow)
                            .map(ship -> ShipDto.builder()
                                    .id(ship.getId())
                                    .name(ship.getName())
                                    .speed(ship.getSpeed())
                                    .iceClass(ship.getIceClass().name())
                                    .build())
                            .toList();
                    boolean waiting = isWaiting(routeSegment);
                    return IcebreakerRouteSegment.builder()
                            .id(routeSegment.getId())
                            .isParking(waiting)
                            .startDate(asLD(routeSegment.getStartDate()))
                            .startPointId(routeSegment.getStartPoint().getId())
                            .startPointName(routeSegment.getStartPoint().getName())
                            .finishDate(asLD(routeSegment.getEndDate()))
                            .finishPointId(routeSegment.getFinishPoint().getId())
                            .finishPointName(routeSegment.getFinishPoint().getName())
                            .ships(companions)
                            .build();
                })
                .toList();
    }

    private static LocalDate asLD(Instant instant) {
        return instant.atOffset(ZoneOffset.UTC).toLocalDate();
    }

    public IcebreakerRouteDto getIcebreakerRoute(long icebreakerId) {
        List<ShipRouteEntity> routeSegments = shipRouteRepository.findAllByShipIdOrderById(icebreakerId);
        List<IcebreakerRouteSegmentDto> segments = routeSegments.stream()
                .map(routeSegment -> IcebreakerRouteSegmentDto.builder()
                        .id(routeSegment.getId())
                        .isParking(isWaiting(routeSegment))
                        .ships(!getCompanions(routeSegment).isEmpty())
                        .routes(getIcebreakerPoints(routeSegment))
                        .build())
                .toList();
        return new IcebreakerRouteDto(segments);
    }

    private List<PointAndTimestamp> getIcebreakerPoints(ShipRouteEntity routeSegment) {
        boolean waiting = isWaiting(routeSegment);
        if (waiting) {
            return List.of(new PointAndTimestamp(routeSegment.getStartPoint().getPoint(),
                            toEpochTruncated(routeSegment.getStartDate())),
                    new PointAndTimestamp(routeSegment.getFinishPoint().getPoint(),
                            toEpochTruncated(routeSegment.getEndDate())));
        }
        List<Point> points = getPoints(routeSegment);
        return shipRouteMapper.createPointAndTimestamp(routeSegment.getStartDate(), points);
    }

    private long toEpochTruncated(Instant instant) {
        return instant.truncatedTo(ChronoUnit.HOURS).getEpochSecond();
    }
}
