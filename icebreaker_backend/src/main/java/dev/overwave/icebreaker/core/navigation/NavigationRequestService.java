package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.user.User;
import dev.overwave.icebreaker.core.user.UserRepository;
import dev.overwave.icebreaker.core.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<NavigationRequestDto> getNavigationRequests(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        if (user.getRole().equals(UserRole.ADMIN)) {
            return navigationRequestRepository.findAll().stream()
                    .map(navigationRequestMapper::toNavigationRequestDto)
                    .toList();
        }
        return user.getShips().stream()
                .flatMap(ship -> ship.getNavigationRequests().stream())
                .filter(request -> request.getStatus() != RequestStatus.APPROVED)
                .map(navigationRequestMapper::toNavigationRequestDto)
                .toList();
    }
}
