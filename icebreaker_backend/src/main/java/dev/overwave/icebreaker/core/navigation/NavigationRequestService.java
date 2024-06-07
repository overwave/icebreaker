package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NavigationRequestService {
    private final NavigationRequestRepository navigationRequestRepository;
    private final ShipRepository shipRepository;
    private final ReferencePointRepository referencePointRepository;

    public NavigationRequestDto saveNavigationRequest(NavigationRequestDto requestDto) {
        Optional<Ship> optionalShip = shipRepository.findById(requestDto.shipId());
        Optional<ReferencePoint> optionalStartPoint = referencePointRepository.findById(requestDto.startPointId());
        Optional<ReferencePoint> optionalFinishPoint = referencePointRepository.findById(requestDto.finishPointId());
        return null;
    }
}
