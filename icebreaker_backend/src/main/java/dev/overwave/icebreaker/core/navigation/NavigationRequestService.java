package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.core.database.LongId;
import dev.overwave.icebreaker.core.ship.Ship;
import dev.overwave.icebreaker.core.ship.ShipRepository;
import dev.overwave.icebreaker.core.user.User;
import dev.overwave.icebreaker.core.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public Map<RequestStatus,List<NavigationRequestDto>> getNavigationRequests(String login) {
        User user = userRepository.findByLoginOrThrow(login);
        List<Long> shipIds = shipRepository.findAllByUserId(user.getId()).stream()
                .map(LongId::getId)
                .toList();
        List<NavigationRequest> requests = navigationRequestRepository.findAllByShipIdIn(shipIds);

        return requests.stream()
                .map(navigationRequestMapper::toNavigationRequestDto)
                .collect(Collectors.groupingBy(NavigationRequestDto::status));
    }
}
