package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestPendingDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestToSaveDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestWithRouteDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestsDtoWithRoute;
import dev.overwave.icebreaker.api.navigation.RouteSegmentDto;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.user.User;
import dev.overwave.icebreaker.core.user.UserRepository;
import dev.overwave.icebreaker.core.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NavigationRequestService {
    private final NavigationRequestRepository navigationRequestRepository;
    private final NavigationRequestMapper navigationRequestMapper;
    private final ShipRepository shipRepository;
    private final NavigationPointRepository navigationPointRepository;
    private final UserRepository userRepository;

    public void saveNavigationRequest(NavigationRequestToSaveDto requestDto) {
        Ship ship = shipRepository.findByIdOrThrow(requestDto.shipId());
        NavigationPoint startPoint = navigationPointRepository.findByIdOrThrow(requestDto.startPointId());
        NavigationPoint finishPoint = navigationPointRepository.findByIdOrThrow(requestDto.finishPointId());
        NavigationRequest navigationRequest = navigationRequestMapper.toNavigationRequest(requestDto, ship,
                startPoint, finishPoint, RequestStatus.PENDING);
        navigationRequestRepository.save(navigationRequest);
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
        return navigationRequestMapper.toNavigationRequestDto(navigationRequestRepository.save(request));
    }
}
