package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.core.ship.Ship;
import org.springframework.stereotype.Component;

@Component
public class NavigationRequestMapper {

    public NavigationRequestDto toNavigationRequestDto(NavigationRequest navigationRequest) {
        return new NavigationRequestDto(navigationRequest.getShip().getId(),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getStartDate(),
                navigationRequest.getStatus());
    }

    public NavigationRequest toNavigationRequest(NavigationRequestDto dto, Ship ship, NavigationPoint startPoint,
                                                 NavigationPoint finishPoint, RequestStatus status) {
        return new NavigationRequest(ship,
                startPoint,
                finishPoint,
                dto.startDate(),
                status);
    }
}
