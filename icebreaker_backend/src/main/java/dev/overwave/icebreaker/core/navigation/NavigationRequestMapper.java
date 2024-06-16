package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestPendingDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestToSaveDto;
import dev.overwave.icebreaker.core.ship.Ship;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;

@Component
public class NavigationRequestMapper {

    public NavigationRequestPendingDto toNavigationRequestPendingDto(NavigationRequest navigationRequest) {
        Ship ship = navigationRequest.getShip();
        return new NavigationRequestPendingDto(
                navigationRequest.getId(),
                navigationRequest.getStatus(),
                ship.getId(),
                ship.getName(),
                ship.getIceClass().name(),
                instantToLocalDate(navigationRequest.getStartDate()),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getStartPoint().getName(),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getFinishPoint().getName());
    }

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

    public NavigationRequest toNavigationRequest(NavigationRequestToSaveDto dto, Ship ship, NavigationPoint startPoint,
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

    public NavigationRequestStatic toNavigationRequestStatic(NavigationRequest request) {
        return NavigationRequestStatic.builder()
                .id(request.getId())
                .shipId(request.getShip().getId())
                .startPointId(request.getStartPoint().getId())
                .finishPointId(request.getFinishPoint().getId())
                .startDate(request.getStartDate())
                .build();
    }
}
