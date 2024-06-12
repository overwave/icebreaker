package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.dto_for_Balya.NavigationRequestDtoForBalya;
import dev.overwave.icebreaker.api.dto_for_Balya.NavigationRequestWithRouteDto;
import dev.overwave.icebreaker.api.dto_for_Balya.NavigationRequestsDtoWithRoute;
import dev.overwave.icebreaker.api.dto_for_Balya.RouteDtoForBalya;
import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
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

    public void saveNavigationRequest(NavigationRequestDto requestDto) {
        Ship ship = shipRepository.findByIdOrThrow(requestDto.shipId());
        NavigationPoint startPoint = navigationPointRepository.findByIdOrThrow(requestDto.startPointId());
        NavigationPoint finishPoint = navigationPointRepository.findByIdOrThrow(requestDto.finishPointId());
        NavigationRequest navigationRequest = navigationRequestMapper.toNavigationRequest(requestDto, ship,
                startPoint, finishPoint, RequestStatus.PENDING);
        navigationRequestRepository.save(navigationRequest);
    }

    public NavigationRequestsDtoWithRoute getNavigationRequests(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        List<NavigationRequest> requests;
        if (user.getRole().equals(UserRole.ADMIN)) {
            requests = navigationRequestRepository.findAll();
        } else {
            requests = user.getShips().stream()
                    .flatMap(ship -> ship.getNavigationRequests().stream())
                    .toList();
        }
        Map<RequestStatus, List<NavigationRequest>> requestsByStatus = requests.stream()
                .collect(Collectors.groupingBy(NavigationRequest::getStatus));
        List<NavigationRequestDtoForBalya> pending = requestsByStatus.get(RequestStatus.PENDING).stream()
                .map(navigationRequestMapper::toNavigationRequestDtoForBalya)
                .toList();
        // хардкодим недостающую инфу
        NavigationRequest first = requestsByStatus.get(RequestStatus.PENDING).getFirst();
        List<Ship> icebreakers = shipRepository.findAllByIcebreaker(true);
        List<RouteDtoForBalya> routes = List.of(navigationRequestMapper.toRouteDtoForBalya(first,
                first.getStartDate().plus(1, ChronoUnit.DAYS), icebreakers.getFirst()));
        List<NavigationRequestWithRouteDto> agreed =
                List.of(navigationRequestMapper.toNavigationRequestWithRouteDto(first, true, routes));
        return new NavigationRequestsDtoWithRoute(
                pending,
                agreed,
                List.of()
        );
    }

    public List<NavigationRequestDtoForBalya> getNavigationRequestsPending(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        if (user.getRole().equals(UserRole.ADMIN)) {
            return navigationRequestRepository.findAllByStatus(RequestStatus.PENDING).stream()
                    .map(navigationRequestMapper::toNavigationRequestDtoForBalya)
                    .toList();
        }
        return user.getShips().stream()
                .flatMap(ship -> ship.getNavigationRequests().stream())
                .filter(request -> request.getStatus() == RequestStatus.PENDING)
                .map(navigationRequestMapper::toNavigationRequestDtoForBalya)
                .toList();
    }
}
