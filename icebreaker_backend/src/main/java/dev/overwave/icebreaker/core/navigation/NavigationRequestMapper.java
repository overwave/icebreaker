package dev.overwave.icebreaker.core.navigation;

import dev.overwave.icebreaker.api.dto_for_Balya.NavigationRequestDtoForBalya;
import dev.overwave.icebreaker.api.dto_for_Balya.NavigationRequestWithRouteDto;
import dev.overwave.icebreaker.api.dto_for_Balya.RouteDtoForBalya;
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
    public RouteDtoForBalya toRouteDtoForBalya(NavigationRequest navigationRequest, Instant finishDate,
                                               Ship icebreaker) {
        return new RouteDtoForBalya(
                1L,
                instantToLocalDate(navigationRequest.getStartDate()),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getStartPoint().getName(),
                instantToLocalDate(finishDate),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getFinishPoint().getName(),
                icebreaker.getName(),
                icebreaker.getIceClass().getDescription() + ", " + icebreaker.getSpeed() + " узлов");

    }

    public NavigationRequestDtoForBalya toNavigationRequestDtoForBalya(NavigationRequest navigationRequest) {
        Ship ship = navigationRequest.getShip();
        return new NavigationRequestDtoForBalya(
                navigationRequest.getId(),
                navigationRequest.getStatus(),
                ship.getId(),
                ship.getName(),
                ship.getIceClass().getDescription() + ", " + ship.getSpeed() + " узлов",
                instantToLocalDate(navigationRequest.getStartDate()),
                navigationRequest.getStartPoint().getId(),
                navigationRequest.getStartPoint().getName(),
                navigationRequest.getFinishPoint().getId(),
                navigationRequest.getFinishPoint().getName());
    }

    public NavigationRequestWithRouteDto toNavigationRequestWithRouteDto(NavigationRequest navigationRequest,
                                                                         boolean convoy,
                                                                         List<RouteDtoForBalya> routes) {
        Ship ship = navigationRequest.getShip();
        return new NavigationRequestWithRouteDto(
                navigationRequest.getId(),
                ship.getId(),
                ship.getName(),
                ship.getIceClass().getDescription() + ", " + ship.getSpeed() + " узлов",
                convoy,
                routes
        );
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
}
