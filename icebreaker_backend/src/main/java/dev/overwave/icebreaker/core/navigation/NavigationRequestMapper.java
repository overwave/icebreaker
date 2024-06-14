package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.navigation.NavigationRequestPendingDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestWithRouteDto;
import dev.overwave.icebreaker.api.navigation.RouteSegmentDto;
import dev.overwave.icebreaker.api.navigation.NavigationRequestDto;
import dev.overwave.icebreaker.core.ship.Ship;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class NavigationRequestMapper {

    //метод-заглушка
    public RouteSegmentDto toRouteSegmentDto(NavigationRequest navigationRequest, Instant finishDate,
                                             Ship icebreaker) {
        return new RouteSegmentDto(
                1L,
                instantToLocalDate(navigationRequest.getStartDate()),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getStartPoint().getName(),
                instantToLocalDate(finishDate),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getFinishPoint().getName(),
                icebreaker.getName(),
                getShipClassDescription(icebreaker));

    }

    public NavigationRequestPendingDto toNavigationRequestPendingDto(NavigationRequest navigationRequest) {
        Ship ship = navigationRequest.getShip();
        return new NavigationRequestPendingDto(
                navigationRequest.getId(),
                navigationRequest.getStatus(),
                ship.getId(),
                ship.getName(),
                getShipClassDescription(ship),
                instantToLocalDate(navigationRequest.getStartDate()),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getStartPoint().getName(),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getFinishPoint().getName());
    }

    public NavigationRequestWithRouteDto toNavigationRequestWithRouteDto(NavigationRequest navigationRequest,
                                                                         boolean convoy,
                                                                         List<RouteSegmentDto> routes) {
        Ship ship = navigationRequest.getShip();
        return new NavigationRequestWithRouteDto(
                navigationRequest.getId(),
                ship.getId(),
                ship.getName(),
                getShipClassDescription(ship),
                convoy,
                routes
        );
    }

    private String getShipClassDescription(Ship ship) {
        return "%s, %s узлов".formatted(ship.getIceClass().getShortDescription(), ship.getSpeed());
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
