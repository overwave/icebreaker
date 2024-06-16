package dev.overwave.icebreaker.core.navigation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestPendingDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestToSaveDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestWithRouteDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestsDtoWithRoute;
import dev.overwave.icebreaker.api.navigation.PointAndTimestamp;
import dev.overwave.icebreaker.api.navigation.RouteSegmentDto;
import dev.overwave.icebreaker.api.navigation.ShipRouteDto;
import dev.overwave.icebreaker.core.geospatial.Point;
import dev.overwave.icebreaker.core.schedule.ContextHolder;
import dev.overwave.icebreaker.core.schedule.ScheduleService;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final ScheduleService scheduleService;
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
        List<NavigationRequestPendingDto> pending = requestsByStatus.getOrDefault(RequestStatus.PENDING, List.of())
                .stream()
                .map(navigationRequestMapper::toNavigationRequestPendingDto)
                .toList();
        return new NavigationRequestsDtoWithRoute(
                pending,
                getApprovedRoutes(requestsByStatus),
                List.of()
        );
    }

    private List<NavigationRequestWithRouteDto> getApprovedRoutes(
            Map<RequestStatus, List<NavigationRequest>> requestsByStatus) {
        // хардкодим недостающую инфу
        List<NavigationRequest> pending = requestsByStatus.get(RequestStatus.PENDING);
        if (pending == null || pending.isEmpty()) {
            return List.of();
        }
        NavigationRequest first = pending.getFirst();
        List<Ship> icebreakers = shipRepository.findAllByIcebreaker(true);
        RouteSegmentDto routeSegmentDto = navigationRequestMapper.toRouteSegmentDto(first,
                first.getStartDate().plus(1, ChronoUnit.DAYS), icebreakers.getFirst());
        return List.of(navigationRequestMapper.toNavigationRequestWithRouteDto(first, true, List.of(routeSegmentDto)));
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
            return navigationRequestRepository.findAllByStatus(RequestStatus.PENDING).stream()
                    .map(navigationRequestMapper::toNavigationRequestPendingDto)
                    .toList();
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
        for (ShipRouteEntity routeSegment : routeSegments) {
            if (isWaiting(routeSegment)) {
                continue;
            }
            List<Long> companions = getCompanions(routeSegment);
            ShipRouteDto route = ShipRouteDto.builder().id(routeSegment.getId())
                    .convoy(!companions.isEmpty())
                    .icebreaker(companions.stream().findFirst().map(String::valueOf).orElse(""))
                    .routes(getHourlyPoints(routeSegment))
                    .build();
            routes.add(route);
        }
        return routes;
    }

    private List<PointAndTimestamp> getHourlyPoints(ShipRouteEntity routeSegment) {
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
            return shipRouteMapper.createPointAndTimestamp(routeSegment.getStartDate(), points);
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
}
