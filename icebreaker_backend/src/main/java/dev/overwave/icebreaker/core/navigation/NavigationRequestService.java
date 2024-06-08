package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NavigationRequestService {
    private final NavigationRequestRepository navigationRequestRepository;
    private final NavigationRequestMapper navigationRequestMapper;
    private final ShipRepository shipRepository;
    private final NavigationPointRepository navigationPointRepository;

    public void saveNavigationRequest(NavigationRequestDto requestDto) {
        Ship ship = shipRepository.findByIdOrThrow(requestDto.shipId());
        NavigationPoint startPoint = navigationPointRepository.findByIdOrThrow(requestDto.startPointId());
        NavigationPoint finishPoint = navigationPointRepository.findByIdOrThrow(requestDto.finishPointId());
        NavigationRequest navigationRequest = navigationRequestMapper.toNavigationRequest(requestDto, ship,
                startPoint, finishPoint);
        navigationRequestRepository.save(navigationRequest);
    }
}
