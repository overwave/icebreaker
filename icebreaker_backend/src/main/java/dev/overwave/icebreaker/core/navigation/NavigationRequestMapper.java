package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import org.springframework.stereotype.Component;

@Component
public class NavigationRequestMapper {

    public NavigationRequestDto toNavigationRequestDto(NavigationRequest navigationRequest) {
        return new NavigationRequestDto(navigationRequest.getShip().getId(),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getStartDate());
    }

    public NavigationRequest toNavigationRequest(NavigationRequestDto dto, Ship ship, NavigationPoint startPoint,
                                                 NavigationPoint finishPoint) {
        return new NavigationRequest(ship,
                startPoint,
                finishPoint,
                dto.startDate());
    }
}
