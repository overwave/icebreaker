package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.core.ship.Ship;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Component
public class NavigationRequestMapper {

    public NavigationRequestDto toNavigationRequestDto(NavigationRequest navigationRequest) {
        return new NavigationRequestDto(navigationRequest.getShip().getId(),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getFinishPoint().getId(),
                instantToLocalDate(navigationRequest.getStartDate()),
                navigationRequest.getStatus());
    }

    private static LocalDate instantToLocalDate(Instant instant) {
        return LocalDate.ofInstant(instant, ZoneOffset.UTC);
    }

    public NavigationRequest toNavigationRequest(NavigationRequestDto dto, Ship ship, NavigationPoint startPoint,
                                                 NavigationPoint finishPoint, RequestStatus status) {
        return new NavigationRequest(ship,
                startPoint,
                finishPoint,
                localDateToInstant(dto.startDate()),
                status);
    }

    private static Instant localDateToInstant(LocalDate localDate) {
        return localDate.atTime(LocalTime.NOON).toInstant(ZoneOffset.UTC);
    }
}
